package com.shifthackz.aisdv1.feature.bonsai

/**
 * Thin Kotlin boundary around the Android Bonsai native library.
 *
 * The bridge centralizes library loading, model probing, generation, progress
 * callbacks, and cancellation so the rest of the feature never calls JNI entry
 * points directly.
 */
internal object BonsaiNativeBridge {

    private val loadResult = runCatching {
        System.loadLibrary(LIBRARY_NAME)
    }

    val isAvailable: Boolean
        get() = loadResult.isSuccess

    fun ensureLoaded() {
        loadResult.getOrElse { error ->
            throw IllegalStateException(
                "Bonsai native runtime is not available on this Android build.",
                error,
            )
        }
    }

    fun probe(layout: AndroidBonsaiModelLayout): String =
        probeModel(
            rootPath = layout.rootPath,
            packedTransformerPath = layout.packedTransformerPath,
            textEncoderPath = layout.textEncoderPath,
            tokenizerPath = layout.tokenizerPath,
            vaePath = layout.vaePath,
            schedulerPath = layout.schedulerPath,
        )

    private external fun probeModel(
        rootPath: String,
        packedTransformerPath: String,
        textEncoderPath: String,
        tokenizerPath: String,
        vaePath: String,
        schedulerPath: String,
    ): String

    fun generate(
        layout: AndroidBonsaiModelLayout,
        prompt: String,
        negativePrompt: String,
        samplingSteps: Int,
        cfgScale: Float,
        width: Int,
        height: Int,
        seed: String,
        batchCount: Int,
        allowNsfw: Boolean,
        backend: String,
        callback: ProgressCallback,
    ): String = generateModel(
        rootPath = layout.rootPath,
        packedTransformerPath = layout.packedTransformerPath,
        textEncoderPath = layout.textEncoderPath,
        tokenizerPath = layout.tokenizerPath,
        vaePath = layout.vaePath,
        schedulerPath = layout.schedulerPath,
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        seed = seed,
        batchCount = batchCount,
        allowNsfw = allowNsfw,
        backend = backend,
        callback = callback,
    )

    private external fun generateModel(
        rootPath: String,
        packedTransformerPath: String,
        textEncoderPath: String,
        tokenizerPath: String,
        vaePath: String,
        schedulerPath: String,
        prompt: String,
        negativePrompt: String,
        samplingSteps: Int,
        cfgScale: Float,
        width: Int,
        height: Int,
        seed: String,
        batchCount: Int,
        allowNsfw: Boolean,
        backend: String,
        callback: ProgressCallback,
    ): String

    external fun interrupt()

    /**
     * JNI callback used by the native runtime to report completed diffusion steps.
     */
    interface ProgressCallback {
        fun onProgress(current: Int, total: Int)
    }
}

private const val LIBRARY_NAME = "sdai_bonsai"
