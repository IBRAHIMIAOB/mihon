package tachiyomi.domain.ai.service

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import tachiyomi.core.common.util.system.logcat
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class NanoBananaProvider(
    private val client: OkHttpClient,
    private val preferences: AIPreferences,
) : AIColoringProvider {

    override val id = "nanobanana"
    override val name = "NanoBanana"

    companion object {
        val SUPPORTED_MODELS = listOf(
            "gemini-2.5-flash-image",
            "gemini-1.5-pro",
            "gemini-1.5-flash",
        )

        val STYLES = mapOf(
            "High-Contrast Cel Shading" to "Use **flat, bold colors** with **hard, sharp edges** for all shadows (high contrast). Employ a maximum of two tones (base color and one shadow tone) per area. The final result should look exactly like **classic anime production art**, with no soft blending or gradients.",
            "Soft & Painterly Digital" to "Color using a **soft-edge brush** and **smooth, gradual blending** between all colors and tones to achieve a highly voluminous and realistic effect. Emphasize complex lighting and subtle color variation (subsurface scattering) for a **fine art illustration** feel.",
            "Watercolor Translucent Wash" to "Color using **highly translucent and thin layers** of color, simulating the texture of paper and the natural bleeding of paint. Allow the black lines to show through clearly. Shadows should be created by **layering multiple thin washes** of color, resulting in a soft, bright, and delicate appearance.",
            "Vibrant Copic Marker Style" to "Apply colors in **solid, highly saturated fields** to mimic alcohol marker application. Use a smooth colorless blender effect for the transitions between mid-tones and highlights. The overall look must be **bright, clean, and graphically vibrant**, with a visible emphasis on crisp edges.",
            "Monochromatic Cyanotype" to "The entire panel must be rendered using a **single-color palette** consisting only of **shades of deep blue, cyan, and white**. Shadows should be rendered through denser areas of blue, creating a classic, limited-palette illustration or **cyanotype print** aesthetic."
        )
    }

    override suspend fun colorize(image: File): Result<File> {
        val apiKey = preferences.aiApiKey().get()
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("API Key is missing"))
        }

        val userPrompt = preferences.aiPrompt().get()
        val model = preferences.aiModel().get()
        val textAction = preferences.aiTextAction().get()
        val targetLanguage = preferences.aiTargetLanguage().get()
        val styleName = preferences.aiStyle().get()
        val styleDescription = STYLES[styleName] ?: STYLES["High-Contrast Cel Shading"]!!

        val textActionInstruction = when(textAction) {
            "translate" -> "Translate This image to $targetLanguage"
            "remove_text" -> "Remove Text from text bubbles"
            "remove_bubbles" -> "Remove Text Bubbles from panels"
            "whiten" -> "Whiten the text bubbles (remove text, keep bubble outline)"
            else -> "Keep text as is"
        }

        val finalPrompt = """
            **$textActionInstruction**

            You are a professional coloring agent specialized in manga panel rendering. Your task is to color the provided black and white image panel with extreme precision.

            You have to follow the following instructions strictly:
            1.  **Rendering Style:** Color this panel using the **$styleName** style.
            2.  **Style Depiction:** Adhere to the following artistic description: **$styleDescription**
            3.  **Core Constraint:** **DO NOT** change the original line-work of the image.
            4.  **Content Constraint:** **DO NOT** add, remove, or alter any elements, objects, or characters in the image.
            5.  **Output:** Only produce the high-resolution, colorized version of the exact input image.
            
            Additional User Instructions:
            $userPrompt
        """.trimIndent()

        // JSON escaping for prompt
        val escapedPrompt = finalPrompt.replace("\"", "\\\"").replace("\n", "\\n")

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

        return try {
            val fileBytes = image.readBytes()
            val base64Image = java.util.Base64.getEncoder().encodeToString(fileBytes)

            // Manual JSON construction
            val jsonBody = """
                {
                  "contents": [{
                    "parts":[
                        {"text": "$escapedPrompt"},
                        {
                          "inline_data": {
                            "mime_type": "image/jpeg",
                            "data": "$base64Image"
                          }
                        }
                    ]
                  }]
                }
            """.trimIndent()

            val request = Request.Builder()
                .url(url)
                .addHeader("x-goog-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.asRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorMsg = response.body?.string() ?: response.message
                response.close()
                return Result.failure(IOException("API Error: $errorMsg"))
            }

            val responseBody = response.body?.string() ?: return Result.failure(IOException("Empty response body"))
            response.close()

            // Extract "data": "..." using regex
            val matchResult = Regex("\"data\": \"([^\"]*)\"").find(responseBody)
            val imageDataBase64 = matchResult?.groups?.get(1)?.value
                ?: return Result.failure(IOException("No image data found in response"))

            val decodedBytes = java.util.Base64.getDecoder().decode(imageDataBase64)
            image.writeBytes(decodedBytes)
            Result.success(image)

        } catch (e: Exception) {
            logcat { "NanoBanana Coloring Failed: ${e.message}" }
            Result.failure(e)
        }
    }
}
