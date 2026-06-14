package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines navigation from the standalone storage usage screen.
 *
 * @author Dmitriy Moroz
 */
interface StorageUsageRouter {
    /**
     * Return from storage usage to the previous screen.
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
object NoOpStorageUsageRouter : StorageUsageRouter {
    override fun navigateBack() = Unit
}
