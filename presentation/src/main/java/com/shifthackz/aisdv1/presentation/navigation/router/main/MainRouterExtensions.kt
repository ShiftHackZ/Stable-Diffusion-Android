package com.shifthackz.aisdv1.presentation.navigation.router.main

import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource

fun MainRouter.postSplashNavigation(action: SplashNavigationUseCase.Action) {
    when (action) {
        SplashNavigationUseCase.Action.LAUNCH_ONBOARDING -> navigateToOnBoarding()
        SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> navigateToServerSetup(
            source = ServerSetupLaunchSource.SPLASH
        )
        SplashNavigationUseCase.Action.LAUNCH_HOME -> navigateToPostSplashConfigLoader()
    }
}
