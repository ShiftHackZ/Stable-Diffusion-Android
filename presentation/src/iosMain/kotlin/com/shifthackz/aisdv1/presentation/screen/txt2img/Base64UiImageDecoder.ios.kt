package com.shifthackz.aisdv1.presentation.screen.txt2img

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Executes the `decodeGeneratedUiImage` step in the SDAI presentation layer.
 *
 * @return Result produced by `decodeGeneratedUiImage`.
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
@OptIn(BetaInteropApi::class, ExperimentalEncodingApi::class, ExperimentalForeignApi::class)
internal fun String.decodeGeneratedUiImage(): UIImage {
    val raw = substringAfter("base64,", this)
    val bytes = Base64.decode(raw)
    val data: NSData = bytes.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = bytes.size.toULong(),
        )
    }
    return UIImage(data = data) ?: error("Unable to decode generated image")
}
