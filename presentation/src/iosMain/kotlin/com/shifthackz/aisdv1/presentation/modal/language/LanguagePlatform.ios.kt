package com.shifthackz.aisdv1.presentation.modal.language

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import platform.Foundation.NSUserDefaults

internal actual fun applyAppLanguage(languageCode: String) {
    Localization.setLanguageCode(languageCode)
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages",
    )
    NSUserDefaults.standardUserDefaults.synchronize()
}

@Composable
internal actual fun LanguageFlagIcon(languageCode: String) = Unit
