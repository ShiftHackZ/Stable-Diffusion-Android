package com.shifthackz.aisdv1.feature.sdxl.runtime

object StableDiffusionCppRuntimeRegistry {

    private var runtime: StableDiffusionCppRuntime? = null

    fun register(value: StableDiffusionCppRuntime) {
        runtime = value
    }

    fun unregister(value: StableDiffusionCppRuntime? = null) {
        if (value == null || runtime === value) {
            runtime = null
        }
    }

    suspend fun generate(
        request: StableDiffusionCppRequest,
        onProgress: (StableDiffusionCppProgress) -> Unit,
    ): StableDiffusionCppResult = runtime
        ?.generate(request, onProgress)
        ?: error("stable-diffusion.cpp runtime is not available on this build.")

    suspend fun interrupt() {
        runtime?.interrupt()
    }
}
