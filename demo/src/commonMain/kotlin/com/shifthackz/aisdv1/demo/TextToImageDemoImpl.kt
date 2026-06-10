package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Implements `TextToImageDemo` behavior in the SDAI demo layer.
 *
 * @author Dmitriy Moroz
 */
internal class TextToImageDemoImpl(
    /**
     * Exposes the `demoDataSerializer` value used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    override val demoDataSerializer: DemoDataSerializer,
    /**
     * Exposes the `timeProvider` value used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    private val timeProvider: TimeProvider,
) : TextToImageDemo, DemoFeature<TextToImagePayload> {

    /**
     * Converts SDAI data with `mapper`.
     *
     * @param input input value consumed by the API.
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    override fun mapper(input: TextToImagePayload, base64: String) = AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = "",
        createdAt = timeProvider.currentTimeMillis(),
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

    /**
     * Loads SDAI data through `getDemoBase64`.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun getDemoBase64(payload: TextToImagePayload) = execute(payload)
}
