package com.shifthackz.aisdv1.presentation.screen.txt2img

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

internal actual fun String.decodeBase64ImageBitmap(): ImageBitmap? =
    runCatching {
        val raw = substringAfter("base64,", this)
        val bytes = Base64.decode(raw, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    }.getOrNull()
