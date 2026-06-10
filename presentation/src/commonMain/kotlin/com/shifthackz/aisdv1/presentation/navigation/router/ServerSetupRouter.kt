package com.shifthackz.aisdv1.presentation.navigation.router

interface ServerSetupRouter {
    fun navigateBack()
    fun navigateToPostSetupConfigLoader()
}

object NoOpServerSetupRouter : ServerSetupRouter {
    override fun navigateBack() = Unit
    override fun navigateToPostSetupConfigLoader() = Unit
}
