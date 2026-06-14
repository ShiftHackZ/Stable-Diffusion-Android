package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `ImageToImageRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ImageToImageRouter {
    /**
     * Executes the `openDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun openDrawer()
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateBack()
    /**
     * Executes the `navigateToServerSetup` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToServerSetup()
    /**
     * Opens the hardware benchmark flow from the local generation guard.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToBenchmark()
    /**
     * Executes the `navigateToImageInPaint` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun navigateToImageInPaint()
    /**
     * Executes the `navigateToGalleryDetails` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun navigateToGalleryDetails(itemId: Long)
    /**
     * Executes the `navigateToReportImage` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun navigateToReportImage(itemId: Long)
}

/**
 * Provides the `NoOpImageToImageRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data object NoOpImageToImageRouter : ImageToImageRouter {
    /**
     * Executes the `openDrawer` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun openDrawer() = Unit
    /**
     * Executes the `navigateBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateBack() = Unit
    /**
     * Executes the `navigateToServerSetup` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToServerSetup() = Unit
    /**
     * Opens the hardware benchmark flow from the local generation guard.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToBenchmark() = Unit
    /**
     * Executes the `navigateToImageInPaint` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun navigateToImageInPaint() = Unit
    /**
     * Executes the `navigateToGalleryDetails` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun navigateToGalleryDetails(itemId: Long) = Unit
    /**
     * Executes the `navigateToReportImage` step in the SDAI presentation layer.
     *
     * @param itemId item id value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun navigateToReportImage(itemId: Long) = Unit
}
