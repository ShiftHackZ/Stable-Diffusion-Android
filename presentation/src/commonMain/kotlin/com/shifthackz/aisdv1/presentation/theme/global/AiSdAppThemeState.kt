package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken

@Immutable
data class AiSdAppThemeState(
    val stateKey: Long = 0L,
    val systemColorPalette: Boolean = false,
    val systemDarkTheme: Boolean = true,
    val darkTheme: Boolean = true,
    val colorToken: ColorToken = ColorToken.MAUVE,
    val darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
) : MviState
