package com.shifthackz.aisdv1.core.imageprocessing

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Input
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter.Output
import com.shifthackz.aisdv1.core.imageprocessing.utils.base64ToBitmap

/**
 * Coordinates `Base64ToBitmapConverter` behavior in the SDAI image processing layer.
 *
 * @author Dmitriy Moroz
 */
class Base64ToBitmapConverter(
    /**
     * Exposes the `fallbackBitmap` value used by the SDAI image processing layer.
     *
     * @author Dmitriy Moroz
     */
    private val fallbackBitmap: Bitmap,
) {

    /**
     * Executes the `invoke` step in the SDAI image processing layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(input: Input): Output =
        convert(input).getOrElse { t ->
            errorLog(t)
            Output(fallbackBitmap)
        }

    /**
     * Executes the `convert` step in the SDAI image processing layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `convert`.
     * @author Dmitriy Moroz
     */
    private fun convert(input: Input): Result<Output> = runCatching {
        Output(base64ToBitmap(input.base64ImageString))
    }

    /**
     * Carries `Input` data through the SDAI image processing layer.
     *
     * @param base64ImageString base64 image string value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Input(val base64ImageString: String)
    /**
     * Carries `Output` data through the SDAI image processing layer.
     *
     * @param bitmap bitmap image processed by the operation.
     * @author Dmitriy Moroz
     */
    data class Output(val bitmap: Bitmap)
}
