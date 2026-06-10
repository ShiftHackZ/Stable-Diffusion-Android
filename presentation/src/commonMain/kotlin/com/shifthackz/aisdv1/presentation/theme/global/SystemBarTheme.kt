package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal expect fun ApplySystemBarTheme(
    colorScheme: ColorScheme,
    isDark: Boolean,
)
