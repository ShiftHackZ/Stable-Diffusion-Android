package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import java.util.*

class ImageToImageDemoImpl(
    override val demoDataSerializer: DemoDataSerializer,
) : ImageToImageDemo, DemoFeature<ImageToImagePayload> {

    override fun mapper(input: ImageToImagePayload, base64: String) = AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = input.base64Image,
        createdAt = Date(),
        type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
        prompt = input.prompt,
        negativePrompt = input.negativePrompt,
        width = input.width,
        height = input.height,
        samplingSteps = input.samplingSteps,
        cfgScale = input.cfgScale,
        restoreFaces = input.restoreFaces,
        sampler = input.sampler,
        seed = System.currentTimeMillis().toString(),
    )

    override fun getDemoBase64(payload: ImageToImagePayload) = execute(payload)
}
