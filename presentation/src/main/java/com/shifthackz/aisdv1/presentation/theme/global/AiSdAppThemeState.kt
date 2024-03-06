package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class AiSdAppThemeState(
    val systemColorPalette: Boolean = false,
    val systemDarkTheme: Boolean = true,
    val darkTheme: Boolean = true,
    val colorToken: ColorToken = ColorToken.MAUVE,
) : MviState
