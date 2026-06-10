package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase

interface OnBoardingRouter {
    fun navigateBack()
    fun navigateToServerSetupAfterOnBoarding()
    fun navigateToPostOnBoardingConfigLoader()
}

object NoOpOnBoardingRouter : OnBoardingRouter {
    override fun navigateBack() = Unit
    override fun navigateToServerSetupAfterOnBoarding() = Unit
    override fun navigateToPostOnBoardingConfigLoader() = Unit
}

fun OnBoardingRouter.postOnBoardingNavigation(
    action: SplashNavigationUseCase.Action,
) = when (action) {
    SplashNavigationUseCase.Action.LAUNCH_ONBOARDING,
    SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> navigateToServerSetupAfterOnBoarding()
    SplashNavigationUseCase.Action.LAUNCH_HOME -> navigateToPostOnBoardingConfigLoader()
}
