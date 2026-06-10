package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Renders the `ApplySystemBarTheme` UI for the SDAI presentation layer.
 *
 * @param colorScheme color scheme value consumed by the API.
 * @param isDark is dark value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun ApplySystemBarTheme(
    colorScheme: ColorScheme,
    isDark: Boolean,
) {
    // iOS bar coloring is intentionally disabled for now. Updating UIKit window
    // colors from a Compose side effect can leave Compose Multiplatform with an
    // empty root view during launch on device/simulator.
}
