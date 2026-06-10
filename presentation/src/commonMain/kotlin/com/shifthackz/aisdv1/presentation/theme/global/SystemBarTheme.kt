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
internal expect fun ApplySystemBarTheme(
    colorScheme: ColorScheme,
    isDark: Boolean,
)
