package com.shifthackz.aisdv1.presentation.modal.language

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import platform.Foundation.NSUserDefaults

/**
 * Executes the `applyAppLanguage` step in the SDAI presentation layer.
 *
 * @param languageCode BCP-47 language code handled by the platform layer.
 * @author Dmitriy Moroz
 */
internal actual fun applyAppLanguage(languageCode: String) {
    Localization.setLanguageCode(languageCode)
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(languageCode),
        forKey = "AppleLanguages",
    )
    NSUserDefaults.standardUserDefaults.synchronize()
}

/**
 * Renders the `LanguageFlagIcon` UI for the SDAI presentation layer.
 *
 * @param languageCode BCP-47 language code handled by the platform layer.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun LanguageFlagIcon(languageCode: String) = Unit
