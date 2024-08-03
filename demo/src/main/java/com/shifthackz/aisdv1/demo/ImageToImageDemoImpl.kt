package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload

internal class ImageToImageDemoImpl(
    override val demoDataSerializer: DemoDataSerializer,
    private val timeProvider: TimeProvider,
) : ImageToImageDemo, DemoFeature<ImageToImagePayload> {

    override fun mapper(input: ImageToImagePayload, base64: String) = AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = input.base64Image,
        createdAt = timeProvider.currentDate(),
        type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
        prompt = input.prompt,
        negativePrompt = input.negativePrompt,
        width = input.width,
        height = input.height,
        samplingSteps = input.samplingSteps,
        cfgScale = input.cfgScale,
        restoreFaces = input.restoreFaces,
        sampler = input.sampler,
        seed = timeProvider.currentTimeMillis().toString(),
        subSeed = timeProvider.currentTimeMillis().toString(),
        subSeedStrength = 0f,
        denoisingStrength = 0f,
    )

    override fun getDemoBase64(payload: ImageToImagePayload) = execute(payload)
}
