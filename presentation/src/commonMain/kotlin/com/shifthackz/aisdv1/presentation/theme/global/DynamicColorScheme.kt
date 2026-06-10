package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Renders the `dynamicColorSchemeOrNull` UI for the SDAI presentation layer.
 *
 * @param useDynamicColors use dynamic colors value consumed by the API.
 * @param isDark is dark value consumed by the API.
 * @return Result produced by `dynamicColorSchemeOrNull`.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun dynamicColorSchemeOrNull(
    useDynamicColors: Boolean,
    isDark: Boolean,
): ColorScheme?
