package com.shifthackz.aisdv1.feature.onnx.environment

import ai.onnxruntime.OrtEnvironment

/**
 * Implements `OrtEnvironmentProvider` behavior in the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class OrtEnvironmentProviderImpl : OrtEnvironmentProvider {

    /**
     * Exposes the `environment` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val environment: OrtEnvironment = OrtEnvironment.getEnvironment()

    /**
     * Loads SDAI data through `get`.
     *
     * @return Result produced by `get`.
     * @author Dmitriy Moroz
     */
    override fun get(): OrtEnvironment {
        return environment
    }
}
