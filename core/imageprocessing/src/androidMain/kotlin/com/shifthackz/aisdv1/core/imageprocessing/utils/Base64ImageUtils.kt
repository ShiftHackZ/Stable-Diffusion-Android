package com.shifthackz.aisdv1.core.imageprocessing.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun base64ToBitmap(base64: String): Bitmap {
    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}

fun base64DefaultToNoWrap(base64Default: String): String {
    val byteArray = Base64.decode(base64Default, Base64.DEFAULT)
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
