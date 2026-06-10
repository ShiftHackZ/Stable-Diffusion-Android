package com.shifthackz.aisdv1.presentation.screen.img2img

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Executes the `cropBase64ImageToSquare` step in the SDAI presentation layer.
 *
 * @param base64 Base64 image payload used by the operation.
 * @return Result produced by `cropBase64ImageToSquare`.
 * @author Dmitriy Moroz
 */
internal actual suspend fun cropBase64ImageToSquare(base64: String): String =
    runCatching {
        val raw = base64.substringAfter("base64,", base64).trim()
        val imageBytes = Base64.decode(raw, Base64.DEFAULT)
        val source = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return@runCatching base64
        if (source.width == source.height && source.width <= MAX_CROPPED_IMAGE_SIDE) {
            return@runCatching base64
        }

        val size = minOf(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val cropped = Bitmap.createBitmap(source, x, y, size, size)
        val output = if (size > MAX_CROPPED_IMAGE_SIDE) {
            Bitmap
                .createScaledBitmap(cropped, MAX_CROPPED_IMAGE_SIDE, MAX_CROPPED_IMAGE_SIDE, true)
                .also { cropped.recycle() }
        } else {
            cropped
        }

        ByteArrayOutputStream().use { stream ->
            output.compress(Bitmap.CompressFormat.JPEG, CROPPED_IMAGE_JPEG_QUALITY, stream)
            Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        }
    }.getOrDefault(base64)

/**
 * Exposes the `MAX_CROPPED_IMAGE_SIDE` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private const val MAX_CROPPED_IMAGE_SIDE = 1536
/**
 * Exposes the `CROPPED_IMAGE_JPEG_QUALITY` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private const val CROPPED_IMAGE_JPEG_QUALITY = 95
