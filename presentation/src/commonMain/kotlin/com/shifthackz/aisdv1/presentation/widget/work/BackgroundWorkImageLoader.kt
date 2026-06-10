package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap

interface BackgroundWorkImageLoader {
    suspend fun load(base64: String): ImageBitmap?
}

object NoOpBackgroundWorkImageLoader : BackgroundWorkImageLoader {
    override suspend fun load(base64: String): ImageBitmap? = null
}
