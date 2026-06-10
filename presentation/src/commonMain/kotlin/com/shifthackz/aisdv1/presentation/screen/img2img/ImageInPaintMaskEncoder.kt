package com.shifthackz.aisdv1.presentation.screen.img2img

/**
 * Executes the `encodeInPaintMaskBase64` step in the SDAI presentation layer.
 *
 * @param imageBase64 image base64 value consumed by the API.
 * @param strokes strokes value consumed by the API.
 * @return Result produced by `encodeInPaintMaskBase64`.
 * @author Dmitriy Moroz
 */
internal expect suspend fun encodeInPaintMaskBase64(
    imageBase64: String,
    strokes: List<InPaintStroke>,
): String?
