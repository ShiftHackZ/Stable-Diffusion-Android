package com.shifthackz.aisdv1.presentation.navigation.router

internal object NoOpDonateRouter : DonateRouter {
    override fun navigateBack() = Unit
}
