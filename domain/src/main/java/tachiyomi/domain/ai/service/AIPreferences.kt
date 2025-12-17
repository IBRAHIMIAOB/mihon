package tachiyomi.domain.ai.service

import tachiyomi.core.common.preference.PreferenceStore

class AIPreferences(
    private val preferenceStore: PreferenceStore,
) {

    fun aiColoringEnabled() = preferenceStore.getBoolean("ai_coloring_enabled", false)

    // General AI preferences 
    fun aiApiKey() = preferenceStore.getString("ai_api_key", "")

    fun aiModel() = preferenceStore.getString("ai_model", "google/gemini-3-pro-image-preview")

    fun aiPrompt() = preferenceStore.getString("ai_prompt", "")

    fun aiTextAction() = preferenceStore.getString("ai_text_action", "none")

    fun aiTargetLanguage() = preferenceStore.getString("ai_target_language", "English")

    fun aiStyle() = preferenceStore.getString("ai_style", "High-Contrast Cel Shading")
}
