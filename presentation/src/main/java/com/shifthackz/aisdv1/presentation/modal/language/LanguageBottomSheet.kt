package com.shifthackz.aisdv1.presentation.modal.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

@Composable
fun LanguageBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
    ) {
        Constants.locales.forEach { (locale, display) ->
            val currentLocale = stringResource(id = R.string.locale)
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                startIcon = Icons.Default.Translate,
                selected = locale == currentLocale,
                text = display.asUiText(),
                showChevron = false,
                onClick = {
                    LocaleListCompat
                        .forLanguageTags(locale)
                        .let(AppCompatDelegate::setApplicationLocales)
                        .also { onDismissRequest() }
                }
            )
        }
    }
}
