package com.shifthackz.aisdv1.core.imageprocessing

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.utils.bitmapToBase64

/**
 * Coordinates `BitmapToBase64Converter` behavior in the SDAI image processing layer.
 *
 * @author Dmitriy Moroz
 */
class BitmapToBase64Converter {

    /**
     * Executes the `invoke` step in the SDAI image processing layer.
     *
     * @param input input value consumed by the API.
     * @author Dmitriy Moroz
     */
    operator fun invoke(input: Input): Output = convert(input).getOrThrow()

    /**
     * Executes the `convert` step in the SDAI image processing layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `convert`.
     * @author Dmitriy Moroz
     */
    private fun convert(input: Input): Result<Output> = runCatching {
        Output(bitmapToBase64(input.bitmap))
    }

    /**
     * Carries `Input` data through the SDAI image processing layer.
     *
     * @param bitmap bitmap image processed by the operation.
     * @author Dmitriy Moroz
     */
    data class Input(val bitmap: Bitmap)
    /**
     * Carries `Output` data through the SDAI image processing layer.
     *
     * @param base64ImageString base64 image string value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Output(val base64ImageString: String)
}
