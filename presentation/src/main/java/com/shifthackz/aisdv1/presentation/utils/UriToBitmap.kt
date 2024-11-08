package com.shifthackz.aisdv1.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.shifthackz.aisdv1.core.common.log.errorLog

fun uriToBitmap(context: Context, uri: Uri): Bitmap? = try {
    val inputStream = context.contentResolver.openInputStream(uri)
    BitmapFactory.decodeStream(inputStream).also { inputStream?.close() }
} catch (e: Exception) {
    errorLog("UrlToBitmap", e)
    null
}
