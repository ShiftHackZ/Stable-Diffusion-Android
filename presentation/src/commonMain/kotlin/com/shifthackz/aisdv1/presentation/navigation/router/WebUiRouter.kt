package com.shifthackz.aisdv1.presentation.navigation.router

interface WebUiRouter {
    fun navigateBack()
}

object NoOpWebUiRouter : WebUiRouter {
    override fun navigateBack() = Unit
}
