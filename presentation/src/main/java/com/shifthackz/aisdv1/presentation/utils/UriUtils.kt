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

fun saveByteArrayToUri(context: Context, uri: Uri, byteArray: ByteArray) = try {
    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
        outputStream.write(byteArray)
    }
} catch (e: Exception) {
    errorLog("SaveByteArrayToUri", e)
}

fun readByteArrayFromUri(context: Context, uri: Uri): ByteArray? = try {
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.readBytes()
    }
} catch (e: Exception) {
    errorLog("ReadByteArrayFromUri", e)
    null
}
