package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Provides the `NoOpSplashRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal object NoOpSplashRouter : SplashRouter {
    /**
     * Executes the `navigateToOnBoardingFromSplash` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToOnBoardingFromSplash() = Unit

    /**
     * Executes the `navigateToServerSetupFromSplash` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToServerSetupFromSplash() = Unit

    /**
     * Executes the `navigateToPostSplashConfigLoader` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToPostSplashConfigLoader() = Unit
}
