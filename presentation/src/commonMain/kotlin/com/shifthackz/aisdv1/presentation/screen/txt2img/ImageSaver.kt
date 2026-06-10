package com.shifthackz.aisdv1.presentation.screen.txt2img

/**
 * Defines the `ImageSaver` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ImageSaver {
    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `save`.
     * @author Dmitriy Moroz
     */
    suspend fun save(base64: String): ImageSaveResult
}

/**
 * Defines the `ImageSharer` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ImageSharer {
    /**
     * Performs the SDAI side effect handled by `share`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `share`.
     * @author Dmitriy Moroz
     */
    suspend fun share(base64: String): ImageShareResult
}

/**
 * Defines the `ImageSaveResult` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ImageSaveResult {
    /**
     * Provides the `Saved` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Saved : ImageSaveResult
    /**
     * Provides the `Unsupported` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unsupported : ImageSaveResult
    /**
     * Carries `Failed` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Failed(val message: String) : ImageSaveResult
}

/**
 * Defines the `ImageShareResult` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ImageShareResult {
    /**
     * Provides the `Sent` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Sent : ImageShareResult
    /**
     * Provides the `Unsupported` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unsupported : ImageShareResult
    /**
     * Carries `Failed` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Failed(val message: String) : ImageShareResult
}

/**
 * Creates the SDAI value produced by `createPlatformImageSaver`.
 *
 * @return Result produced by `createPlatformImageSaver`.
 * @author Dmitriy Moroz
 */
expect fun createPlatformImageSaver(): ImageSaver

/**
 * Creates the SDAI value produced by `createPlatformImageSharer`.
 *
 * @return Result produced by `createPlatformImageSharer`.
 * @author Dmitriy Moroz
 */
expect fun createPlatformImageSharer(): ImageSharer
