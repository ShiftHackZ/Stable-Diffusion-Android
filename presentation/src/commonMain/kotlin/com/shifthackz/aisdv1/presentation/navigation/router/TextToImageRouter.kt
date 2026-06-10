package com.shifthackz.aisdv1.presentation.navigation.router

/**
 * Defines the `TextToImageRouter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface TextToImageRouter {
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
 * Provides the `NoOpTextToImageRouter` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data object NoOpTextToImageRouter : TextToImageRouter {
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
