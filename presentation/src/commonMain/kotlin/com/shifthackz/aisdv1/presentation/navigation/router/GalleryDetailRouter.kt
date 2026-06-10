package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `GalleryDetailRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GalleryDetailRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
    /**
     * Executes the `navigateToTextToImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToTextToImage()
    /**
     * Executes the `navigateToImageToImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToImageToImage()
    /**
     * Executes the `navigateToReportImage` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun navigateToReportImage(itemId: Long)
}

/**
 * Provides the `NoOpGalleryDetailRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpGalleryDetailRouter : GalleryDetailRouter {
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
    /**
     * Executes the `navigateToTextToImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToTextToImage() = Unit
    /**
     * Executes the `navigateToImageToImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToImageToImage() = Unit
    /**
     * Executes the `navigateToReportImage` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun navigateToReportImage(itemId: Long) = Unit
}
