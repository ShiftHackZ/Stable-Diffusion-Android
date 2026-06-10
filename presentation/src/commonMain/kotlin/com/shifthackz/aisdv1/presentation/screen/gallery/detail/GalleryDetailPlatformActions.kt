package com.shifthackz.aisdv1.presentation.screen.gallery.detail

/**
 * Defines the `GalleryDetailPlatformActions` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GalleryDetailPlatformActions {
    /**
     * Performs the SDAI side effect handled by `saveImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `saveImage`.
     * @author Dmitriy Moroz
     */
    suspend fun saveImage(base64: String): GalleryDetailActionResult
    /**
     * Performs the SDAI side effect handled by `shareImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `shareImage`.
     * @author Dmitriy Moroz
     */
    suspend fun shareImage(base64: String): GalleryDetailActionResult
    /**
     * Performs the SDAI side effect handled by `shareText`.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `shareText`.
     * @author Dmitriy Moroz
     */
    suspend fun shareText(text: String): GalleryDetailActionResult
    /**
     * Executes the `copyText` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `copyText`.
     * @author Dmitriy Moroz
     */
    suspend fun copyText(text: String): GalleryDetailActionResult
}

/**
 * Defines the `GalleryDetailActionResult` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GalleryDetailActionResult {
    /**
     * Provides the `Done` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Done : GalleryDetailActionResult
    /**
     * Provides the `Unsupported` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unsupported : GalleryDetailActionResult
    /**
     * Carries `Failed` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Failed(val message: String) : GalleryDetailActionResult
}

/**
 * Provides the `NoOpGalleryDetailPlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpGalleryDetailPlatformActions : GalleryDetailPlatformActions {
    /**
     * Performs the SDAI side effect handled by `saveImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `saveImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun saveImage(base64: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported

    /**
     * Performs the SDAI side effect handled by `shareImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `shareImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun shareImage(base64: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported

    /**
     * Performs the SDAI side effect handled by `shareText`.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `shareText`.
     * @author Dmitriy Moroz
     */
    override suspend fun shareText(text: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported

    /**
     * Executes the `copyText` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `copyText`.
     * @author Dmitriy Moroz
     */
    override suspend fun copyText(text: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported
}

/**
 * Creates the SDAI value produced by `createDefaultGalleryDetailPlatformActions`.
 *
 * @return Result produced by `createDefaultGalleryDetailPlatformActions`.
 * @author Dmitriy Moroz
 */
expect fun createDefaultGalleryDetailPlatformActions(): GalleryDetailPlatformActions
