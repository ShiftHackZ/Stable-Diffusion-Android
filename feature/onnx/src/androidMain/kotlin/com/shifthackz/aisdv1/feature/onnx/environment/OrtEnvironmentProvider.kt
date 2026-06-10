package com.shifthackz.aisdv1.feature.onnx.environment

import ai.onnxruntime.OrtEnvironment

/**
 * Executes the `function` step in the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal fun interface OrtEnvironmentProvider {
    fun get(): OrtEnvironment
}
