package com.shifthackz.aisdv1.presentation.modal.language

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization

/**
 * Executes the `applyAppLanguage` step in the SDAI presentation layer.
 *
 * @param languageCode BCP-47 language code handled by the platform layer.
 * @author Dmitriy Moroz
 */
internal actual fun applyAppLanguage(languageCode: String) {
    Localization.setLanguageCode(languageCode)
}

/**
 * Renders the `LanguageFlagIcon` UI for the SDAI presentation layer.
 *
 * @param languageCode BCP-47 language code handled by the platform layer.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun LanguageFlagIcon(languageCode: String) {
    Localization.getCountryFlagDrawableResId(languageCode)?.let { drawableResId ->
        Image(
            modifier = Modifier.padding(horizontal = 8.dp),
            painter = painterResource(id = drawableResId),
            contentDescription = languageCode,
        )
    }
}
