package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken

@Immutable
data class OnBoardingState(
    val darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
    val appVersion: String = "",
    val showLocalDiffusionPage: Boolean = false,
) : MviState
