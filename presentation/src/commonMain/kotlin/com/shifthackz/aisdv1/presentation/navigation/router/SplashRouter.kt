package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase

/**
 * Defines the `SplashRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface SplashRouter {
    /**
     * Executes the `navigateToOnBoardingFromSplash` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToOnBoardingFromSplash()

    /**
     * Executes the `navigateToServerSetupFromSplash` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToServerSetupFromSplash()

    /**
     * Executes the `navigateToPostSplashConfigLoader` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToPostSplashConfigLoader()
}

/**
 * Executes the `postSplashNavigation` step in the SDAI presentation layer.
 *
 * @param action action value consumed by the API.
 * @author Dmitriy Moroz
 */
fun SplashRouter.postSplashNavigation(
    action: SplashNavigationUseCase.Action,
) {
    when (action) {
        SplashNavigationUseCase.Action.LAUNCH_ONBOARDING -> navigateToOnBoardingFromSplash()
        SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> navigateToServerSetupFromSplash()
        SplashNavigationUseCase.Action.LAUNCH_HOME -> navigateToPostSplashConfigLoader()
    }
}
