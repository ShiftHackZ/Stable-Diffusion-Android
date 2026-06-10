package com.shifthackz.aisdv1.feature.onnx.environment

import ai.onnxruntime.OrtEnvironment

internal fun interface OrtEnvironmentProvider {
    fun get(): OrtEnvironment
}
