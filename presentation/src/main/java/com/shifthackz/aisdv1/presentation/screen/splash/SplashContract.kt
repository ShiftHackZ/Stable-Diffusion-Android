package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase

sealed interface SplashEffect : MviEffect {
    object LaunchOnBoarding : SplashEffect
    object LaunchServerSetup : SplashEffect
    object LaunchHome : SplashEffect
}

fun SplashNavigationUseCase.Action.toEffect(): SplashEffect = when (this) {
    SplashNavigationUseCase.Action.LAUNCH_ONBOARDING -> SplashEffect.LaunchOnBoarding
    SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> SplashEffect.LaunchServerSetup
    SplashNavigationUseCase.Action.LAUNCH_HOME -> SplashEffect.LaunchHome
}
