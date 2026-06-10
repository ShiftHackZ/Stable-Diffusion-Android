package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase

interface SplashRouter {
    fun navigateToOnBoardingFromSplash()

    fun navigateToServerSetupFromSplash()

    fun navigateToPostSplashConfigLoader()
}

fun SplashRouter.postSplashNavigation(
    action: SplashNavigationUseCase.Action,
) {
    when (action) {
        SplashNavigationUseCase.Action.LAUNCH_ONBOARDING -> navigateToOnBoardingFromSplash()
        SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> navigateToServerSetupFromSplash()
        SplashNavigationUseCase.Action.LAUNCH_HOME -> navigateToPostSplashConfigLoader()
    }
}
