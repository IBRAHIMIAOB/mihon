package eu.kanade.presentation.more.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import eu.kanade.presentation.more.settings.Preference
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import tachiyomi.domain.ai.service.AIPreferences
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import tachiyomi.domain.ai.service.NanoBananaProvider

object SettingsAIScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.pref_ai_coloring // Need to add string

    @Composable
    override fun getPreferences(): List<Preference> {
        val aiPreferences = remember { Injekt.get<AIPreferences>() }
        
        // For now hardcoding strings as I can't easily add to strings.xml without finding it
        // and usually these apps use Multi-Language framework.
        // I will use placeholder strings where MR is expected, or raw strings if possible.
        // But getTitleRes expects StringResource (Int). 
        // I will reuse a generic string or one from the system if I can't add one.
        // However, standard string adding in Android requires modifying values/strings.xml.
        // I will assume I can't add resources easily and use existing ones for Title if possible,
        // or just add a dummy MR identifier if I can modify the MR generation or if it's dynamic.
        // Tachiyomi uses Moko Resources (MR).
        // I will try to use a dummy `MR.strings.app_name` for Title temporarily if I can't add new one,
        // or actually I should add to `i18n` strings.
        
        return listOf(
            getGeneralGroup(aiPreferences),
            getProviderGroup(aiPreferences)
        )
    }

    @Composable
    private fun getGeneralGroup(aiPreferences: AIPreferences): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.pref_ai_coloring),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.SwitchPreference(
                    preference = aiPreferences.aiColoringEnabled(),
                    title = stringResource(MR.strings.pref_ai_coloring),
                    subtitle = stringResource(MR.strings.pref_ai_coloring_summary)
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = aiPreferences.aiProvider(),
                    entries = persistentMapOf(
                        "nanobanana" to "NanoBanana (Gemini)",
                        "chatgpt" to "ChatGPT",
                        "grok" to "Grok"
                    ),
                    title = stringResource(MR.strings.pref_ai_provider)
                )
            )
        )
    }

    @Composable
    private fun getProviderGroup(aiPreferences: AIPreferences): Preference.PreferenceGroup {
        val provider by aiPreferences.aiProvider().collectAsState()
        val textAction by aiPreferences.aiTextAction().collectAsState()

        val models = when (provider) {
            "nanobanana" -> NanoBananaProvider.SUPPORTED_MODELS.associateWith { it }
            "chatgpt" -> mapOf("gpt-4o" to "GPT-4o", "gpt-4-turbo" to "GPT-4 Turbo")
            "grok" -> mapOf("grok-beta" to "Grok Beta")
            else -> emptyMap()
        }.toPersistentMap()

        val styles = NanoBananaProvider.STYLES.keys.associateWith { it }.toPersistentMap()

        return Preference.PreferenceGroup(
            title = when (provider) {
                "nanobanana" -> "NanoBanana (Gemini) Settings"
                "chatgpt" -> "ChatGPT Settings"
                "grok" -> "Grok Settings"
                else -> "AI Provider Settings"
            },
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.ListPreference(
                    preference = aiPreferences.aiModel(),
                    entries = models,
                    title = "Model",
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = aiPreferences.aiStyle(),
                    entries = styles,
                    title = "Style",
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = aiPreferences.aiTextAction(),
                    entries = persistentMapOf(
                        "none" to "None (Keep Text)",
                        "translate" to "Translate",
                        "remove_text" to "Remove Text (Keep Bubbles)",
                        "remove_bubbles" to "Remove Bubbles",
                        "whiten" to "Whiten Bubbles"
                    ),
                    title = "Text Action",
                ),
            ).mutate {
                if (textAction == "translate") {
                    add(
                        Preference.PreferenceItem.EditTextPreference(
                            preference = aiPreferences.aiTargetLanguage(),
                            title = "Target Language",
                            subtitle = "Language to translate to",
                        )
                    )
                }
                add(
                    Preference.PreferenceItem.EditTextPreference(
                        preference = aiPreferences.aiApiKey(),
                        title = stringResource(MR.strings.pref_ai_api_key),
                        subtitle = stringResource(MR.strings.pref_ai_api_key_summary)
                    )
                )
                add(
                    Preference.PreferenceItem.EditTextPreference(
                        preference = aiPreferences.aiPrompt(),
                        title = "Additional Instructions (Optional)",
                        subtitle = "Append extra instructions to the prompt"
                    )
                )
            }
        )
    }
}
