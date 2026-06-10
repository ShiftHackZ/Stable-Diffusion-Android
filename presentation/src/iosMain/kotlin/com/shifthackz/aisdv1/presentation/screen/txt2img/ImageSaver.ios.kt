package com.shifthackz.aisdv1.presentation.screen.txt2img

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Photos.PHAccessLevelAddOnly
import platform.Photos.PHAssetCreationRequest
import platform.Photos.PHAssetResourceTypePhoto
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.coroutines.resume

actual fun createPlatformImageSaver(): ImageSaver = IosImageSaver()

private class IosImageSaver : ImageSaver {

    override suspend fun save(base64: String): ImageSaveResult {
        val data = runCatching { base64.decodeGeneratedImageData() }.getOrElse { t ->
            return ImageSaveResult.Failed(t.message ?: "Unable to decode generated image")
        }

        return suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                if (!continuation.isActive) return@dispatch_async
                PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelAddOnly) { status ->
                    if (status != PHAuthorizationStatusAuthorized && status != PHAuthorizationStatusLimited) {
                        if (continuation.isActive) {
                            continuation.resume(ImageSaveResult.Failed("Photos permission was not granted"))
                        }
                        return@requestAuthorizationForAccessLevel
                    }

                    PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                        changeBlock = {
                            PHAssetCreationRequest.creationRequestForAsset()
                                .addResourceWithType(PHAssetResourceTypePhoto, data, options = null)
                        },
                        completionHandler = { success, error ->
                            val result = if (success) {
                                ImageSaveResult.Saved
                            } else {
                                ImageSaveResult.Failed(
                                    error?.localizedDescription ?: "Unable to save image to Photos",
                                )
                            }
                            if (continuation.isActive) {
                                continuation.resume(result)
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(BetaInteropApi::class, ExperimentalEncodingApi::class, ExperimentalForeignApi::class)
private fun String.decodeGeneratedImageData(): NSData {
    val raw = substringAfter("base64,", this).filterNot(Char::isWhitespace)
    val bytes = Base64.Default.decode(raw)
    return bytes.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = bytes.size.toULong(),
        )
    }
}
