package tachiyomi.domain.ai.service

object AIPromptBuilder {

    val STYLES = mapOf(
        "High-Contrast Cel Shading" to "Use **flat, bold colors** with **hard, sharp edges** for all shadows (high contrast). Employ a maximum of two tones (base color and one shadow tone) per area. The final result should look exactly like **classic anime production art**, with no soft blending or gradients.",
        "Soft & Painterly Digital" to "Color using a **soft-edge brush** and **smooth, gradual blending** between all colors and tones to achieve a highly voluminous and realistic effect. Emphasize complex lighting and subtle color variation (subsurface scattering) for a **fine art illustration** feel.",
        "Watercolor Translucent Wash" to "Color using **highly translucent and thin layers** of color, simulating the texture of paper and the natural bleeding of paint. Allow the black lines to show through clearly. Shadows should be created by **layering multiple thin washes** of color, resulting in a soft, bright, and delicate appearance.",
        "Vibrant Copic Marker Style" to "Apply colors in **solid, highly saturated fields** to mimic alcohol marker application. Use a smooth colorless blender effect for the transitions between mid-tones and highlights. The overall look must be **bright, clean, and graphically vibrant**, with a visible emphasis on crisp edges.",
        "Monochromatic Cyanotype" to "The entire panel must be rendered using a **single-color palette** consisting only of **shades of deep blue, cyan, and white**. Shadows should be rendered through denser areas of blue, creating a classic, limited-palette illustration or **cyanotype print** aesthetic.",
        "Natural" to "Colorize the image naturally giving you **full freedom** to choose colors, shading, and lighting based on the visual context of the scene. **Do not** force any specific artistic stylization, fixed palette, or artificial technique. The goal is a balanced, standard rendering that fits the content."
    )

    fun buildPrompt(
        textAction: String,
        targetLanguage: String,
        styleName: String,
        userPrompt: String
    ): String {
        val styleDescription = STYLES[styleName] ?: STYLES["High-Contrast Cel Shading"]!!

        val textActionInstruction = when (textAction) {
            "translate" -> "Translate This image to $targetLanguage"
            "remove_text" -> "Remove Text from text bubbles"
            "remove_bubbles" -> "Remove Text Bubbles from panels"
            "whiten" -> "Whiten the text bubbles (remove text, keep bubble outline)"
            else -> "Keep text as is"
        }

        return """
            **$textActionInstruction**

            You are a professional image enhancement agent specialized in manga panel rendering. Your task is to enhance/color the provided black and white image panel with extreme precision.

            You have to follow the following instructions strictly:
            1.  **Rendering Style:** Enhance/Color this panel using the **$styleName** style.
            2.  **Style Depiction:** Adhere to the following artistic description: **$styleDescription**
            3.  **Core Constraint:** **DO NOT** change the original line-work of the image.
            4.  **Content Constraint:** **DO NOT** add, remove, or alter any elements, objects, or characters in the image.
            5.  **Output:** Only produce the high-resolution, colorized version of the exact input image.
            
            Additional User Instructions:
            $userPrompt
        """.trimIndent()
    }
}
