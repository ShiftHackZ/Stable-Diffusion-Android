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

internal expect fun applyAppLanguage(languageCode: String)

@Composable
internal expect fun LanguageFlagIcon(languageCode: String)
