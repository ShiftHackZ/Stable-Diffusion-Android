package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun ApplySystemBarTheme(
    colorScheme: ColorScheme,
    isDark: Boolean,
) {
    // iOS bar coloring is intentionally disabled for now. Updating UIKit window
    // colors from a Compose side effect can leave Compose Multiplatform with an
    // empty root view during launch on device/simulator.
}
