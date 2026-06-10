package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter

class AndroidBackgroundWorkImageLoader(
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
) : BackgroundWorkImageLoader {

    override suspend fun load(base64: String): ImageBitmap =
        base64ToBitmapConverter(Base64ToBitmapConverter.Input(base64)).bitmap.asImageBitmap()
}
