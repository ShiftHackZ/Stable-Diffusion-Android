package com.shifthackz.aisdv1.presentation.navigation.router

internal object NoOpSplashRouter : SplashRouter {
    override fun navigateToOnBoardingFromSplash() = Unit

    override fun navigateToServerSetupFromSplash() = Unit

    override fun navigateToPostSplashConfigLoader() = Unit
}
