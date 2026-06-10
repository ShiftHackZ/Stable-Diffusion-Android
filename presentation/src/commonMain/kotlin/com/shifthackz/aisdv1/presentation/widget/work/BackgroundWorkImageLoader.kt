package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Defines the `BackgroundWorkImageLoader` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface BackgroundWorkImageLoader {
    /**
     * Loads SDAI data through `load`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `load`.
     * @author Dmitriy Moroz
     */
    suspend fun load(base64: String): ImageBitmap?
}

/**
 * Provides the `NoOpBackgroundWorkImageLoader` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpBackgroundWorkImageLoader : BackgroundWorkImageLoader {
    /**
     * Loads SDAI data through `load`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `load`.
     * @author Dmitriy Moroz
     */
    override suspend fun load(base64: String): ImageBitmap? = null
}
