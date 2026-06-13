package com.shifthackz.aisdv1.feature.sdxl.runtime

data class StableDiffusionCppRequest(
    val modelPath: String,
    val prompt: String,
    val negativePrompt: String,
    val width: Int,
    val height: Int,
    val samplingSteps: Int,
    val cfgScale: Float,
    val seed: Long,
    val batchCount: Int,
    val backend: StableDiffusionCppBackend = StableDiffusionCppBackend.AUTO,
    val sampler: StableDiffusionCppSampler = StableDiffusionCppSampler.EULER,
)

data class StableDiffusionCppProgress(
    val current: Int,
    val total: Int,
)

data class StableDiffusionCppResult(
    val base64Image: String,
)

interface StableDiffusionCppRuntime {
    suspend fun generate(
        request: StableDiffusionCppRequest,
        onProgress: (StableDiffusionCppProgress) -> Unit,
    ): StableDiffusionCppResult

    suspend fun interrupt()
}
