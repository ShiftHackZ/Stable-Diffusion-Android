package com.shifthackz.aisdv1.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun colors(
    light: Color,
    dark: Color,
): Color {
    return if (isSystemInDarkTheme()) dark else light
}
