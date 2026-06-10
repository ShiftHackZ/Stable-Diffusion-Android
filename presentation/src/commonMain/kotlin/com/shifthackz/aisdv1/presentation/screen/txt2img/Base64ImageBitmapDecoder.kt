package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Converts SDAI data with `decodeBase64ImageBitmap`.
 *
 * @return Result produced by `decodeBase64ImageBitmap`.
 * @author Dmitriy Moroz
 */
internal expect fun String.decodeBase64ImageBitmap(): ImageBitmap?
