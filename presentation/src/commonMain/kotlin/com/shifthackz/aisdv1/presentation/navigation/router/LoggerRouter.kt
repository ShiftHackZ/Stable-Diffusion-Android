package com.shifthackz.aisdv1.presentation.navigation.router

interface LoggerRouter {
    fun navigateBack()
}

object NoOpLoggerRouter : LoggerRouter {
    override fun navigateBack() = Unit
}
