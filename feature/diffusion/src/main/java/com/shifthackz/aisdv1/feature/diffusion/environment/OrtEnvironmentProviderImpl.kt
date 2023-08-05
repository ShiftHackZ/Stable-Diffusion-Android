package com.shifthackz.aisdv1.feature.diffusion.environment

import ai.onnxruntime.OrtEnvironment
import com.shifthackz.aisdv1.core.common.log.debugLog

internal class OrtEnvironmentProviderImpl : OrtEnvironmentProvider {

    private val environment: OrtEnvironment = OrtEnvironment.getEnvironment()

    init {
        debugLog("Initialized ORT, instance is $environment")
    }

    override fun get(): OrtEnvironment {
        return environment
    }
}
