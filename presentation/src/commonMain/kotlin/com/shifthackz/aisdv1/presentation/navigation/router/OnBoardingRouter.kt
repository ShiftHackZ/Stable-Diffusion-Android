package com.shifthackz.aisdv1.presentation.navigation.router

import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase

/**
 * Defines the `OnBoardingRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface OnBoardingRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
    /**
     * Executes the `navigateToServerSetupAfterOnBoarding` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToServerSetupAfterOnBoarding()
    /**
     * Executes the `navigateToPostOnBoardingConfigLoader` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToPostOnBoardingConfigLoader()
}

/**
 * Provides the `NoOpOnBoardingRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpOnBoardingRouter : OnBoardingRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
    /**
     * Executes the `navigateToServerSetupAfterOnBoarding` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToServerSetupAfterOnBoarding() = Unit
    /**
     * Executes the `navigateToPostOnBoardingConfigLoader` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToPostOnBoardingConfigLoader() = Unit
}

/**
 * Executes the `postOnBoardingNavigation` step in the SDAI presentation layer.
 *
 * @param action action value consumed by the API.
 * @author Dmitriy Moroz
 */
fun OnBoardingRouter.postOnBoardingNavigation(
    action: SplashNavigationUseCase.Action,
) = when (action) {
    SplashNavigationUseCase.Action.LAUNCH_ONBOARDING,
    SplashNavigationUseCase.Action.LAUNCH_SERVER_SETUP -> navigateToServerSetupAfterOnBoarding()
    SplashNavigationUseCase.Action.LAUNCH_HOME -> navigateToPostOnBoardingConfigLoader()
}
