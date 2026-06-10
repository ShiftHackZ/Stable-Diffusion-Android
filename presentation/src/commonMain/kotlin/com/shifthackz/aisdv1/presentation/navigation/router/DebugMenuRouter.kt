package com.shifthackz.aisdv1.presentation.navigation.router

interface DebugMenuRouter {
    fun navigateBack()
    fun navigateToLogger()
}

object NoOpDebugMenuRouter : DebugMenuRouter {
    override fun navigateBack() = Unit
    override fun navigateToLogger() = Unit
}
