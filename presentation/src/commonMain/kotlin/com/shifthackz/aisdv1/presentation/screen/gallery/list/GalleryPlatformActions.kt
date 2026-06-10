package com.shifthackz.aisdv1.presentation.screen.gallery.list

/**
 * Defines the `GalleryPlatformActions` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GalleryPlatformActions {
    /**
     * Executes the `openMediaStoreFolder` step in the SDAI presentation layer.
     *
     * @param folderUri folder uri value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun openMediaStoreFolder(folderUri: String)
    /**
     * Performs the SDAI side effect handled by `shareExport`.
     *
     * @param filePath file path value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun shareExport(filePath: String)
}

/**
 * Provides the `NoOpGalleryPlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpGalleryPlatformActions : GalleryPlatformActions {
    /**
     * Executes the `openMediaStoreFolder` step in the SDAI presentation layer.
     *
     * @param folderUri folder uri value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun openMediaStoreFolder(folderUri: String) = Unit
    /**
     * Performs the SDAI side effect handled by `shareExport`.
     *
     * @param filePath file path value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun shareExport(filePath: String) = Unit
}
