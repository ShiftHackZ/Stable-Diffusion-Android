package com.shifthackz.aisdv1.presentation.screen.txt2img

interface ImageSaver {
    suspend fun save(base64: String): ImageSaveResult
}

interface ImageSharer {
    suspend fun share(base64: String): ImageShareResult
}

sealed interface ImageSaveResult {
    data object Saved : ImageSaveResult
    data object Unsupported : ImageSaveResult
    data class Failed(val message: String) : ImageSaveResult
}

sealed interface ImageShareResult {
    data object Sent : ImageShareResult
    data object Unsupported : ImageShareResult
    data class Failed(val message: String) : ImageShareResult
}

expect fun createPlatformImageSaver(): ImageSaver

expect fun createPlatformImageSharer(): ImageSharer
