package com.shifthackz.aisdv1.data.gateway.mediastore

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.Photos.PHAccessLevelAddOnly
import platform.Photos.PHAssetCreationRequest
import platform.Photos.PHAssetResourceTypePhoto
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Coordinates `IosMediaStoreGateway` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class IosMediaStoreGateway : MediaStoreGateway {

    /**
     * Executes the `exportToFile` step in the SDAI data layer.
     *
     * @param fileName file name value consumed by the API.
     * @param content content value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun exportToFile(fileName: String, content: ByteArray) {
        val data = content.toNSData()
        dispatch_async(dispatch_get_main_queue()) {
            PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelAddOnly) { status ->
                if (status != PHAuthorizationStatusAuthorized && status != PHAuthorizationStatusLimited) {
                    return@requestAuthorizationForAccessLevel
                }

                PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                    changeBlock = {
                        PHAssetCreationRequest.creationRequestForAsset()
                            .addResourceWithType(PHAssetResourceTypePhoto, data, options = null)
                    },
                    completionHandler = { _, _ -> },
                )
            }
        }
    }

    /**
     * Loads SDAI data through `getInfo`.
     *
     * @author Dmitriy Moroz
     */
    override fun getInfo(): MediaStoreInfo = MediaStoreInfo()
}

/**
 * Converts SDAI data with `toNSData`.
 *
 * @return Result produced by `toNSData`.
 * @author Dmitriy Moroz
 */
@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData =
    usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = size.toULong(),
        )
    }
