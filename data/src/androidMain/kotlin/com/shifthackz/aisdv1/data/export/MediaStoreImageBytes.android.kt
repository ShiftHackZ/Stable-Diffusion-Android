package com.shifthackz.aisdv1.data.export

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64ToBitmap
import java.io.ByteArrayOutputStream

/**
 * Converts SDAI data with `toMediaStoreImageBytes`.
 *
 * @return Result produced by `toMediaStoreImageBytes`.
 * @author Dmitriy Moroz
 */
internal actual fun String.toMediaStoreImageBytes(): ByteArray {
    val stream = ByteArrayOutputStream()
    base64ToBitmap(this).compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
