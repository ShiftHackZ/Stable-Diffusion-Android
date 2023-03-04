package com.shifthackz.aisdv1.presentation.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun base64ToImage(base64: String) : Bitmap {
    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
