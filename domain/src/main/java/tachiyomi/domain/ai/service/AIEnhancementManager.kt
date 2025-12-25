package tachiyomi.domain.ai.service

import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import android.util.Base64
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.IOException

class AIEnhancementManager(
    private val preferences: AIPreferences
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    fun isEnabled(): Boolean {
        return preferences.aiEnhancementsEnabled().get()
    }

    suspend fun enhanceImage(image: File, customPrompt: String? = null): Result<File> {
        val apiKey = preferences.aiApiKey().get()
        if (apiKey.isBlank()) {
            return Result.failure(Exception("API Key is missing"))
        }

        if (!image.exists()) {
             return Result.failure(Exception("Input image not found"))
        }

        return try {
            val base64Image = Base64.encodeToString(image.readBytes(), Base64.NO_WRAP)
            val mimeType = when (image.extension.lowercase()) {
                "png" -> "image/png"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                else -> "image/jpeg"
            }
            val imageUrl = "data:$mimeType;base64,$base64Image"
            val modelName = preferences.aiModel().get()
            
            val userPrompt = customPrompt ?: preferences.aiPrompt().get()
            val textAction = preferences.aiTextAction().get()
            val targetLanguage = preferences.aiTargetLanguage().get()
            val styleName = preferences.aiStyle().get()

            val prompt = AIPromptBuilder.buildPrompt(
                textAction = textAction,
                targetLanguage = targetLanguage,
                styleName = styleName,
                userPrompt = userPrompt
            )

            val payload = buildJsonObject {
                put("model", modelName)
                putJsonArray("messages") {
                    add(buildJsonObject {
                        put("role", "user")
                        putJsonArray("content") {
                            add(buildJsonObject {
                                put("type", "text")
                                put("text", prompt)
                            })
                            add(buildJsonObject {
                                put("type", "image_url")
                                put("image_url", buildJsonObject {
                                    put("url", imageUrl)
                                })
                            })
                        }
                    })
                }
            }

            val jsonPayload = buildJsonObject {
                 put("model", modelName)
                 putJsonArray("messages") {
                     add(buildJsonObject {
                         put("role", "user")
                         putJsonArray("content") {
                             add(buildJsonObject {
                                 put("type", "text")
                                 put("text", prompt)
                             })
                             add(buildJsonObject {
                                 put("type", "image_url")
                                 put("image_url", buildJsonObject {
                                     put("url", imageUrl)
                                 })
                             })
                         }
                     })
                 }
                 putJsonArray("modalities") {
                     add("image")
                     add("text")
                 }
            }


            val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                return Result.failure(Exception("API request failed: ${response.code} - $errorBody"))
            }

            val responseBody = response.body?.string() ?: return Result.failure(Exception("Empty response"))
            val responseJson = json.parseToJsonElement(responseBody).jsonObject
            
            val choices = responseJson["choices"]?.jsonArray
            if (choices.isNullOrEmpty()) {
                 return Result.failure(Exception("No choices in response"))
            }
            
            val message = choices[0].jsonObject["message"]?.jsonObject
            // OpenRouter/Gemini image response format might differ. 
            // User script: output_image_url = response_data.get('choices', [])[0].get('message', {}).get('images', [])[0].get('image_url', {}).get('url', None)
            // It seems it returns 'images' array inside 'message'. This is non-standard OpenAI format (usually 'content' has text or 'data' for images).
            // But if the model is 'image-preview', maybe it returns this structure.
            
            val images = message?.get("images")?.jsonArray
            val outputImageUrl = images?.get(0)?.jsonObject?.get("image_url")?.jsonObject?.get("url")?.jsonPrimitive?.content
            
            if (outputImageUrl != null) {
                // Decode base64
                // expecting data:image/...;base64,.....
                val base64Data = outputImageUrl.substringAfter(",")
                val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                
                val outputDir = image.parentFile
                val outputFile = File(outputDir, "enhanced_${image.name}")
                outputFile.writeBytes(imageBytes)
                
                return Result.success(outputFile)
            } else {
                 return Result.failure(Exception("No image URL in response: $responseBody"))
            }

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
