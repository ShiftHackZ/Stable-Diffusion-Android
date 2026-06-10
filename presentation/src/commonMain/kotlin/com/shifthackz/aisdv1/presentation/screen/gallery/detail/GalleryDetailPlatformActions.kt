package com.shifthackz.aisdv1.presentation.screen.gallery.detail

interface GalleryDetailPlatformActions {
    suspend fun saveImage(base64: String): GalleryDetailActionResult
    suspend fun shareImage(base64: String): GalleryDetailActionResult
    suspend fun shareText(text: String): GalleryDetailActionResult
    suspend fun copyText(text: String): GalleryDetailActionResult
}

sealed interface GalleryDetailActionResult {
    data object Done : GalleryDetailActionResult
    data object Unsupported : GalleryDetailActionResult
    data class Failed(val message: String) : GalleryDetailActionResult
}

object NoOpGalleryDetailPlatformActions : GalleryDetailPlatformActions {
    override suspend fun saveImage(base64: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported

    override suspend fun shareImage(base64: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported

    override suspend fun shareText(text: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported

    override suspend fun copyText(text: String): GalleryDetailActionResult =
        GalleryDetailActionResult.Unsupported
}

expect fun createDefaultGalleryDetailPlatformActions(): GalleryDetailPlatformActions
