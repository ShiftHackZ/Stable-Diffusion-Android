package com.shifthackz.aisdv1.feature.onnx.environment

/**
 * Executes the `function` step in the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
fun interface LocalModelIdProvider {
    fun get(): String
}
