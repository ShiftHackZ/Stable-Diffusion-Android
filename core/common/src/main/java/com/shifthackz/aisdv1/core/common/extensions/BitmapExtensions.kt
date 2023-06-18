package com.shifthackz.aisdv1.core.common.extensions

import android.graphics.Bitmap
import android.graphics.Canvas

fun randomColorBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(randomColor())
    return bitmap
}
