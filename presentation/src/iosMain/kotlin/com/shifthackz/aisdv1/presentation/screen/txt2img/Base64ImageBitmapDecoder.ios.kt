package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.skia.Image as SkiaImage

@OptIn(ExperimentalEncodingApi::class)
internal actual fun String.decodeBase64ImageBitmap(): ImageBitmap? =
    runCatching {
        val raw = substringAfter("base64,", this)
        val bytes = Base64.decode(raw)
        SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
    }.getOrNull()
