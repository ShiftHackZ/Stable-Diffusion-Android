package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Composable

/**
 * Defines the `ImageToImagePlatformActions` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface ImageToImagePlatformActions {
    /**
     * Executes the `pickImage` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @return Result produced by `pickImage`.
     * @author Dmitriy Moroz
     */
    suspend fun pickImage(source: ImageToImagePickSource): ImageToImagePickResult
}

/**
 * Coordinates `ImageToImagePickSource` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
enum class ImageToImagePickSource {
    Camera,
    Gallery,
}

/**
 * Defines the `ImageToImagePickResult` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ImageToImagePickResult {
    /**
     * Carries `Selected` data through the SDAI presentation layer.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    data class Selected(val base64: String) : ImageToImagePickResult
    /**
     * Provides the `Cancelled` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Cancelled : ImageToImagePickResult
    /**
     * Provides the `Unsupported` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unsupported : ImageToImagePickResult
    /**
     * Carries `Failed` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Failed(val message: String) : ImageToImagePickResult
}

/**
 * Provides the `NoOpImageToImagePlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data object NoOpImageToImagePlatformActions : ImageToImagePlatformActions {
    /**
     * Executes the `pickImage` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @return Result produced by `pickImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun pickImage(source: ImageToImagePickSource): ImageToImagePickResult =
        ImageToImagePickResult.Unsupported
}

/**
 * Renders the `rememberImageToImagePlatformActions` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberImageToImagePlatformActions`.
 * @author Dmitriy Moroz
 */
@Composable
expect fun rememberImageToImagePlatformActions(): ImageToImagePlatformActions
