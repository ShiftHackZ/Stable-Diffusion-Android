package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `GalleryRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GalleryRouter {
    /**
     * Executes the `openDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun openDrawer()
    /**
     * Executes the `navigateToGalleryDetails` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun navigateToGalleryDetails(itemId: Long)
}

/**
 * Provides the `NoOpGalleryRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpGalleryRouter : GalleryRouter {
    /**
     * Executes the `openDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun openDrawer() = Unit
    /**
     * Executes the `navigateToGalleryDetails` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun navigateToGalleryDetails(itemId: Long) = Unit
}
