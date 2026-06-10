package com.shifthackz.aisdv1.core.imageprocessing.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Converts SDAI data with `base64ToBitmap`.
 *
 * @param base64 Base64 image payload used by the operation.
 * @return Result produced by `base64ToBitmap`.
 * @author Dmitriy Moroz
 */
fun base64ToBitmap(base64: String): Bitmap {
    val imageBytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

/**
 * Converts SDAI data with `bitmapToBase64`.
 *
 * @param bitmap bitmap image processed by the operation.
 * @return Result produced by `bitmapToBase64`.
 * @author Dmitriy Moroz
 */
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}

/**
 * Executes the `base64DefaultToNoWrap` step in the SDAI image processing layer.
 *
 * @param base64Default base64 default value consumed by the API.
 * @return Result produced by `base64DefaultToNoWrap`.
 * @author Dmitriy Moroz
 */
fun base64DefaultToNoWrap(base64Default: String): String {
    val byteArray = Base64.decode(base64Default, Base64.DEFAULT)
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
