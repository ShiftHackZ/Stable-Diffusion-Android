package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Composable

interface ImageToImagePlatformActions {
    suspend fun pickImage(source: ImageToImagePickSource): ImageToImagePickResult
}

enum class ImageToImagePickSource {
    Camera,
    Gallery,
}

sealed interface ImageToImagePickResult {
    data class Selected(val base64: String) : ImageToImagePickResult
    data object Cancelled : ImageToImagePickResult
    data object Unsupported : ImageToImagePickResult
    data class Failed(val message: String) : ImageToImagePickResult
}

data object NoOpImageToImagePlatformActions : ImageToImagePlatformActions {
    override suspend fun pickImage(source: ImageToImagePickSource): ImageToImagePickResult =
        ImageToImagePickResult.Unsupported
}

@Composable
expect fun rememberImageToImagePlatformActions(): ImageToImagePlatformActions
