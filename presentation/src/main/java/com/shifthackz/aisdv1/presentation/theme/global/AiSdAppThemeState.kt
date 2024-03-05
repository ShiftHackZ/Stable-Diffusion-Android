package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.runtime.Immutable
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class AiSdAppThemeState(
    val useSystemColorPalette: Boolean = false,
    val useSystemDarkTheme: Boolean = true,
    val useDarkTheme: Boolean = true,
) : MviState
