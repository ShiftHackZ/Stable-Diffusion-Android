package com.shifthackz.aisdv1.feature.diffusion.environment

import ai.onnxruntime.OrtEnvironment

internal fun interface OrtEnvironmentProvider {
    fun get(): OrtEnvironment
}
