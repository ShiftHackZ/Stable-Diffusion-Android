package com.shifthackz.aisdv1.presentation.screen.img2img

internal expect suspend fun encodeInPaintMaskBase64(
    imageBase64: String,
    strokes: List<InPaintStroke>,
): String?
