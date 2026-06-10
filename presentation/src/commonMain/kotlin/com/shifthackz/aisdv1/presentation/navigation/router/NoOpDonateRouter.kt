package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Provides the `NoOpDonateRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal object NoOpDonateRouter : DonateRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
}
