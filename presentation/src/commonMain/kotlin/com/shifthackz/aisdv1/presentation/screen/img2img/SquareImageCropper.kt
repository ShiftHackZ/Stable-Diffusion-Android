package com.shifthackz.aisdv1.presentation.screen.img2img

/**
 * Executes the `cropBase64ImageToSquare` step in the SDAI presentation layer.
 *
 * @param base64 Base64 image payload used by the operation.
 * @return Result produced by `cropBase64ImageToSquare`.
 * @author Dmitriy Moroz
 */
internal expect suspend fun cropBase64ImageToSquare(base64: String): String
