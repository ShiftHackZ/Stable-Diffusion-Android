package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter

/**
 * Coordinates `AndroidBackgroundWorkImageLoader` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class AndroidBackgroundWorkImageLoader(
    /**
     * Exposes the `base64ToBitmapConverter` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : BackgroundWorkImageLoader {

    /**
     * Loads SDAI data through `load`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `load`.
     * @author Dmitriy Moroz
     */
    override suspend fun load(base64: String): ImageBitmap =
        base64ToBitmapConverter(Base64ToBitmapConverter.Input(base64)).bitmap.asImageBitmap()
}
