package com.shifthackz.aisdv1.feature.sdxl

internal object StableDiffusionCppNativeBridge {

    private val loadResult = runCatching {
        System.loadLibrary(LIBRARY_NAME)
    }

    fun ensureLoaded() {
        loadResult.getOrElse { error ->
            throw IllegalStateException(
                "stable-diffusion.cpp native runtime is not available.",
                error,
            )
        }
    }

    external fun generate(
        modelPath: String,
        prompt: String,
        negativePrompt: String,
        width: Int,
        height: Int,
        samplingSteps: Int,
        cfgScale: Float,
        seed: Long,
        batchCount: Int,
        backend: Int,
        sampler: Int,
        shape: IntArray,
        callback: ProgressCallback,
    ): ByteArray

    external fun interrupt()

    interface ProgressCallback {
        fun onProgress(current: Int, total: Int)
    }
}

private const val LIBRARY_NAME = "sdai_sdcpp"
