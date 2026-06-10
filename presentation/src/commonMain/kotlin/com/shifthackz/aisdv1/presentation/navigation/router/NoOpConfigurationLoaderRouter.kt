package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Provides the `NoOpConfigurationLoaderRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal object NoOpConfigurationLoaderRouter : ConfigurationLoaderRouter {
    /**
     * Renders the `navigateToHomeScreen` UI for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToHomeScreen() = Unit
}
