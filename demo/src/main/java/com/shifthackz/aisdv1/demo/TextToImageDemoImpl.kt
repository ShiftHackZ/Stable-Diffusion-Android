package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

internal class TextToImageDemoImpl(
    override val demoDataSerializer: DemoDataSerializer,
    private val timeProvider: TimeProvider,
) : TextToImageDemo, DemoFeature<TextToImagePayload> {

    override fun mapper(input: TextToImagePayload, base64: String) = AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = "",
        createdAt = timeProvider.currentDate(),
        type = AiGenerationResult.Type.TEXT_TO_IMAGE,
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
        hidden = false,
    )

    override fun getDemoBase64(payload: TextToImagePayload) = execute(payload)
}
