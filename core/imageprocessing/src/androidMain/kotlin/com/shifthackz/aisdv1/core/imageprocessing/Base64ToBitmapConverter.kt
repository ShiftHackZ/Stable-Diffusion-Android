package com.shifthackz.aisdv1.core.imageprocessing

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64ToBitmap

class Base64ToBitmapConverter(
    private val fallbackBitmap: Bitmap,
) {

    operator fun invoke(input: Input): Output =
        convert(input).getOrElse { t ->
            errorLog(t)
            Output(fallbackBitmap)
        }

    private fun convert(input: Input): Result<Output> = runCatching {
        Output(base64ToBitmap(input.base64ImageString))
    }

    data class Input(val base64ImageString: String)
    data class Output(val bitmap: Bitmap)
}
