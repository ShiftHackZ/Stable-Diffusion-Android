package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines navigation from the standalone network usage screen.
 *
 * @author Dmitriy Moroz
 */
interface NetworkUsageRouter {
    /**
     * Return from network usage to the previous screen.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
}

/**
 * Preview/test fallback used before the real app router is bound.
 *
 * @author Dmitriy Moroz
 */
object NoOpNetworkUsageRouter : NetworkUsageRouter {
    override fun navigateBack() = Unit
}
