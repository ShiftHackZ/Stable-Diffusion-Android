package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.ArliAiSampler
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.ArliAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.ArliAiTextToImageRequest

/**
 * Converts SDAI data with `mapToArliAiRequest`.
 *
 * @param model model checkpoint value consumed by the API.
 * @return Result produced by `mapToArliAiRequest`.
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToArliAiRequest(model: String): ArliAiTextToImageRequest = with(this) {
    ArliAiTextToImageRequest(
        sdModelCheckpoint = model,
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps.coerceIn(MIN_STEPS, MAX_STEPS),
        samplerName = sampler.ifBlank { ArliAiSampler.default.key },
        width = width,
        height = height,
        seed = seed.mapToArliAiSeed(),
        cfgScale = cfgScale,
        batchSize = batchCount.coerceAtLeast(1),
        restoreFaces = restoreFaces,
        detailerEnabled = aDetailer.enabled.takeIf { it },
        detailerPrompt = aDetailer.promptIfEnabled(),
        detailerNegative = aDetailer.negativePromptIfEnabled(),
        detailerSteps = samplingSteps.coerceIn(MIN_STEPS, MAX_STEPS).takeIf { aDetailer.enabled },
        detailerStrength = aDetailer.denoisingStrengthIfEnabled(),
        detailerModel = aDetailer.modelIfEnabled(),
        detailerConfidence = aDetailer.confidenceIfEnabled(),
        detailerPadding = aDetailer.paddingIfEnabled(),
        detailerBlur = aDetailer.blurIfEnabled(),
    )
}

/**
 * Converts SDAI data with `mapToArliAiRequest`.
 *
 * @param model model checkpoint value consumed by the API.
 * @return Result produced by `mapToArliAiRequest`.
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapToArliAiRequest(model: String): ArliAiImageToImageRequest = with(this) {
    ArliAiImageToImageRequest(
        sdModelCheckpoint = model,
        prompt = prompt,
        negativePrompt = negativePrompt,
        initImages = listOf(base64Image),
        mask = base64MaskImage.takeIf(String::isNotBlank),
        denoisingStrength = denoisingStrength,
        steps = samplingSteps.coerceIn(MIN_STEPS, MAX_STEPS),
        samplerName = sampler.ifBlank { ArliAiSampler.default.key },
        width = width,
        height = height,
        seed = seed.mapToArliAiSeed(),
        cfgScale = cfgScale,
        batchSize = batchCount.coerceAtLeast(1),
        restoreFaces = restoreFaces,
        maskBlur = maskBlur,
        inPaintingFill = inPaintingFill,
        inPaintFullRes = inPaintFullRes,
        inPaintFullResPadding = inPaintFullResPadding,
        inPaintingMaskInvert = inPaintingMaskInvert,
        detailerEnabled = aDetailer.enabled.takeIf { it },
        detailerPrompt = aDetailer.promptIfEnabled(),
        detailerNegative = aDetailer.negativePromptIfEnabled(),
        detailerSteps = samplingSteps.coerceIn(MIN_STEPS, MAX_STEPS).takeIf { aDetailer.enabled },
        detailerStrength = aDetailer.denoisingStrengthIfEnabled(),
        detailerModel = aDetailer.modelIfEnabled(),
        detailerConfidence = aDetailer.confidenceIfEnabled(),
        detailerPadding = aDetailer.paddingIfEnabled(),
        detailerBlur = aDetailer.blurIfEnabled(),
    )
}

private fun String.mapToArliAiSeed(): Long? =
    trim().takeIf(String::isNotEmpty)?.toLongOrNull()

private fun ADetailerConfig.promptIfEnabled() =
    prompt.takeIf { enabled }

private fun ADetailerConfig.negativePromptIfEnabled() =
    negativePrompt.takeIf { enabled }

private fun ADetailerConfig.denoisingStrengthIfEnabled() =
    denoisingStrength.takeIf { enabled }

private fun ADetailerConfig.modelIfEnabled() =
    model.takeIf { enabled }

private fun ADetailerConfig.confidenceIfEnabled() =
    confidence.takeIf { enabled }

private fun ADetailerConfig.paddingIfEnabled() =
    inpaintPadding.takeIf { enabled }

private fun ADetailerConfig.blurIfEnabled() =
    maskBlur.takeIf { enabled }

private const val MIN_STEPS = 1
private const val MAX_STEPS = 40
