package eu.kanade.presentation.more.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import tachiyomi.presentation.core.util.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import eu.kanade.presentation.more.settings.Preference
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.PersistentList
import tachiyomi.domain.ai.service.AIPreferences
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import tachiyomi.domain.ai.service.AIPromptBuilder

object SettingsAIScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.pref_ai_enhancements

    @Composable
    override fun getPreferences(): List<Preference> {
        val aiPreferences = remember { Injekt.get<AIPreferences>() }

        return listOf(
            getGeneralGroup(aiPreferences),
            getSettingsGroup(aiPreferences)
        )
    }

    @Composable
    private fun getGeneralGroup(aiPreferences: AIPreferences): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.pref_ai_enhancements),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.SwitchPreference(
                    preference = aiPreferences.aiEnhancementsEnabled(),
                    title = stringResource(MR.strings.pref_ai_enhancements),
                    subtitle = stringResource(MR.strings.pref_ai_enhancements_summary)
                )
            )
        )
    }

    @Composable
    private fun getSettingsGroup(aiPreferences: AIPreferences): Preference.PreferenceGroup {
        val textAction by aiPreferences.aiTextAction().collectAsState()

        val styles = AIPromptBuilder.STYLES.keys.associateWith { it }.toPersistentMap()

        val baseItems: PersistentList<Preference.PreferenceItem<out Any, out Any>> = persistentListOf(
                Preference.PreferenceItem.EditTextPreference(
                    preference = aiPreferences.aiModel(),
                    title = stringResource(MR.strings.pref_ai_model),
                    subtitle = "OpenRouter Image Gen Model ID (e.g. google/gemini-pro-1.5)"
                ),
                Preference.PreferenceItem.ListPreference(
                    preference = aiPreferences.aiStyle(),
                    entries = styles,
                    title = stringResource(MR.strings.pref_ai_style),
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
                    title = stringResource(MR.strings.pref_ai_text_action),
                ),
            )

        val items = baseItems.builder()

        if (textAction == "translate") {
            items.add(
                Preference.PreferenceItem.EditTextPreference(
                    preference = aiPreferences.aiTargetLanguage(),
                    title = stringResource(MR.strings.pref_ai_target_language),
                    subtitle = stringResource(MR.strings.pref_ai_target_language_summary),
                )
            )
        }
        items.add(
            Preference.PreferenceItem.EditTextPreference(
                preference = aiPreferences.aiApiKey(),
                title = stringResource(MR.strings.pref_ai_api_key),
                subtitle = stringResource(MR.strings.pref_ai_api_key_summary)
            )
        )
        items.add(
            Preference.PreferenceItem.EditTextPreference(
                preference = aiPreferences.aiPrompt(),
                title = stringResource(MR.strings.pref_ai_additional_instructions),
                subtitle = stringResource(MR.strings.pref_ai_additional_instructions_summary)
            )
        )

        return Preference.PreferenceGroup(
            title = "OpenRouter Settings",
            preferenceItems = items.build()
        )
    }
}
