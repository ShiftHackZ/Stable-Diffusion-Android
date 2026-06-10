package com.shifthackz.aisdv1.presentation.modal.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

/**
 * Renders the `LanguageBottomSheet` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param onLanguageSelected callback invoked by the component.
 * @param onDismissRequest callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
fun LanguageBottomSheet(
    modifier: Modifier = Modifier,
    onLanguageSelected: (String) -> Unit = { applyAppLanguage(it) },
    onDismissRequest: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
    ) {
        Localization.entries.forEach { language ->
            val locale = language.code
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                selected = locale == Localization.currentLanguageCode(),
                text = language.name.asUiText(),
                showChevron = false,
                startIconContent = {
                    LanguageFlagIcon(locale)
                },
                onClick = {
                    onLanguageSelected(locale)
                    onDismissRequest()
                }
            )
        }
    }
}

/**
 * Executes the `applyAppLanguage` step in the SDAI presentation layer.
 *
 * @param languageCode BCP-47 language code handled by the platform layer.
 * @author Dmitriy Moroz
 */
internal expect fun applyAppLanguage(languageCode: String)

/**
 * Renders the `LanguageFlagIcon` UI for the SDAI presentation layer.
 *
 * @param languageCode BCP-47 language code handled by the platform layer.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun LanguageFlagIcon(languageCode: String)
