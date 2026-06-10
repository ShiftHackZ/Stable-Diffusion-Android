package com.shifthackz.aisdv1.presentation.theme.global

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Renders the `dynamicColorSchemeOrNull` UI for the SDAI presentation layer.
 *
 * @param useDynamicColors use dynamic colors value consumed by the API.
 * @param isDark is dark value consumed by the API.
 * @return Result produced by `dynamicColorSchemeOrNull`.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun dynamicColorSchemeOrNull(
    useDynamicColors: Boolean,
    isDark: Boolean,
): ColorScheme? {
    if (!useDynamicColors || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    val context = LocalContext.current
    return if (isDark) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }
}
