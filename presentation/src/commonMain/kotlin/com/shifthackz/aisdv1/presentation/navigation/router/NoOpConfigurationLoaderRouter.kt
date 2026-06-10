package com.shifthackz.aisdv1.presentation.navigation.router

internal object NoOpConfigurationLoaderRouter : ConfigurationLoaderRouter {
    override fun navigateToHomeScreen() = Unit
}
