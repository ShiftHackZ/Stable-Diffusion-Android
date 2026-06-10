package com.shifthackz.aisdv1.core.imageprocessing

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter.Input
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter.Output
import com.shifthackz.aisdv1.core.imageprocessing.utils.bitmapToBase64

class BitmapToBase64Converter {

    operator fun invoke(input: Input): Output = convert(input).getOrThrow()

    private fun convert(input: Input): Result<Output> = runCatching {
        Output(bitmapToBase64(input.bitmap))
    }

    data class Input(val bitmap: Bitmap)
    data class Output(val base64ImageString: String)
}
