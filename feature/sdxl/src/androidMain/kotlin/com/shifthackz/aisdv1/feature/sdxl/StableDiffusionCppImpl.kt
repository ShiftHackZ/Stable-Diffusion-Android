package com.shifthackz.aisdv1.feature.sdxl

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.sdxl.StableDiffusionCpp
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppBackend
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppRequest
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppRuntime
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppSampler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart

internal class StableDiffusionCppImpl(
    private val runtime: StableDiffusionCppRuntime,
) : StableDiffusionCpp {

    private val statusFlow = MutableSharedFlow<LocalDiffusionStatus>(extraBufferCapacity = 64)

    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String {
        statusFlow.tryEmit(LocalDiffusionStatus(current = 0, total = payload.samplingSteps))
        return runtime
            .generate(
                request = payload.toStableDiffusionCppRequest(modelPath),
                onProgress = { progress ->
                    statusFlow.tryEmit(
                        LocalDiffusionStatus(
                            current = progress.current,
                            total = progress.total,
                        ),
                    )
                },
            )
            .base64Image
    }

    override suspend fun interrupt() {
        runtime.interrupt()
        statusFlow.tryEmit(LocalDiffusionStatus(current = 0, total = 0))
    }

    override fun observeStatus(): Flow<LocalDiffusionStatus> = statusFlow
        .onStart { emit(LocalDiffusionStatus(current = 0, total = 0)) }
}

private fun TextToImagePayload.toStableDiffusionCppRequest(
    modelPath: String,
): StableDiffusionCppRequest = StableDiffusionCppRequest(
    modelPath = modelPath,
    prompt = prompt,
    negativePrompt = negativePrompt,
    width = width,
    height = height,
    samplingSteps = samplingSteps,
    cfgScale = cfgScale,
    seed = seed.toLongOrNull() ?: -1L,
    batchCount = batchCount.coerceAtLeast(1),
    backend = sdxlBackend.toStableDiffusionCppBackend(),
    sampler = StableDiffusionCppSampler.parse(sampler),
)

private fun SdxlBackend.toStableDiffusionCppBackend(): StableDiffusionCppBackend = when (this) {
    SdxlBackend.AUTO -> StableDiffusionCppBackend.AUTO
    SdxlBackend.CPU -> StableDiffusionCppBackend.CPU
    SdxlBackend.OPEN_CL -> StableDiffusionCppBackend.OPEN_CL
    SdxlBackend.VULKAN -> StableDiffusionCppBackend.VULKAN
}
