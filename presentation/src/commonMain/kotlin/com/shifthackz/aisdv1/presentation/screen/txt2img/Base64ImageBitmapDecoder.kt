package com.shifthackz.aisdv1.presentation.screen.txt2img

import androidx.compose.ui.graphics.ImageBitmap

internal expect fun String.decodeBase64ImageBitmap(): ImageBitmap?
