package com.shifthackz.aisdv1.presentation.screen.img2img

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.Surface

@OptIn(ExperimentalEncodingApi::class)
internal actual suspend fun cropBase64ImageToSquare(base64: String): String =
    runCatching {
        val raw = base64.substringAfter("base64,", base64).trim()
        val source = Image.makeFromEncoded(Base64.decode(raw))
        if (source.width == source.height && source.width <= MAX_CROPPED_IMAGE_SIDE) {
            return@runCatching base64
        }

        val size = minOf(source.width, source.height)
        val outputSize = minOf(size, MAX_CROPPED_IMAGE_SIDE)
        val x = ((source.width - size) / 2).toFloat()
        val y = ((source.height - size) / 2).toFloat()
        val surface = Surface.makeRasterN32Premul(outputSize, outputSize)
        surface.canvas.drawImageRect(
            image = source,
            src = Rect.makeXYWH(x, y, size.toFloat(), size.toFloat()),
            dst = Rect.makeWH(outputSize.toFloat(), outputSize.toFloat()),
            samplingMode = SamplingMode.LINEAR,
            paint = null,
            strict = true,
        )

        val data = surface.makeImageSnapshot()
            .encodeToData(EncodedImageFormat.JPEG, CROPPED_IMAGE_JPEG_QUALITY)
            ?: return@runCatching base64
        Base64.encode(data.bytes)
    }.getOrDefault(base64)

private const val MAX_CROPPED_IMAGE_SIDE = 1536
private const val CROPPED_IMAGE_JPEG_QUALITY = 95
