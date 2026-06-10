package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiSampler
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.model.StabilityTextPromptRaw
import com.shifthackz.aisdv1.network.request.StabilityTextToImageRequest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Converts SDAI data with `mapToStabilityAiRequest`.
 *
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToStabilityAiRequest(): StabilityTextToImageRequest = with(this) {
    StabilityTextToImageRequest(
        height = height,
        width = width,
        textPrompts = buildList {
            addAll(prompt.mapToStabilityPrompt(1.0))
            addAll(negativePrompt.mapToStabilityPrompt(-1.0))
        },
        cfgScale = cfgScale,
        clipGuidancePreset = (stabilityAiClipGuidance ?: StabilityAiClipGuidance.NONE).toString(),
        sampler = sampler
            .takeIf { it != "${StabilityAiSampler.NONE}" }
            .takeIf { StabilityAiSampler.entries.map { s -> "$s" }.contains(it) },
        seed = seed.toLongOrNull()?.coerceIn(0L..4294967295L) ?: 0L,
        steps = samplingSteps,
        stylePreset = stabilityAiStylePreset?.takeIf { it != StabilityAiStylePreset.NONE }?.key,
    )
}

/**
 * Converts SDAI data with `mapToStabilityAiRequest`.
 *
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapToStabilityAiRequest() = with(this) {
    buildMap {
        buildList {
            addAll(prompt.mapToStabilityPrompt(1.0))
            addAll(negativePrompt.mapToStabilityPrompt(-1.0))
        }.forEachIndexed { index, stpRaw ->
            this["text_prompts[$index][text]"] = stpRaw.text
            this["text_prompts[$index][weight]"] = stpRaw.weight.toString()
        }
        this["image_strength"] = "$denoisingStrength"
        this["cfg_scale"] = "$cfgScale"
        this["clip_guidance_preset"] = (stabilityAiClipGuidance ?: StabilityAiClipGuidance.NONE).toString()
        this["seed"] = (seed.toLongOrNull()?.coerceIn(0L..4294967295L) ?: 0L).toString()
        this["steps"] = "$samplingSteps"
        stabilityAiStylePreset?.takeIf { it != StabilityAiStylePreset.NONE }?.key?.let {
            this["style_preset"] = it
        }
    }
}

/**
 * Converts SDAI data with `mapStabilityTextToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapStabilityTextToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, String>.mapStabilityTextToImageResult(
    createdAtMillis: Long,
): AiGenerationResult {
    val (payload, base64) = this
    return AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = "",
        createdAt = createdAtMillis,
        type = AiGenerationResult.Type.TEXT_TO_IMAGE,
        denoisingStrength = 0f,
        prompt = payload.prompt,
        negativePrompt = payload.negativePrompt,
        width = payload.width,
        height = payload.height,
        samplingSteps = payload.samplingSteps,
        cfgScale = payload.cfgScale,
        restoreFaces = payload.restoreFaces,
        sampler = payload.sampler,
        seed = payload.seed,
        subSeed = payload.subSeed,
        subSeedStrength = payload.subSeedStrength,
        hidden = false,
    )
}

/**
 * Converts SDAI data with `mapStabilityImageToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapStabilityImageToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<ImageToImagePayload, String>.mapStabilityImageToImageResult(
    createdAtMillis: Long,
): AiGenerationResult {
    val (payload, base64) = this
    return AiGenerationResult(
        id = 0L,
        image = base64,
        inputImage = payload.base64Image,
        createdAt = createdAtMillis,
        type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
        denoisingStrength = payload.denoisingStrength,
        prompt = payload.prompt,
        negativePrompt = payload.negativePrompt,
        width = payload.width,
        height = payload.height,
        samplingSteps = payload.samplingSteps,
        cfgScale = payload.cfgScale,
        restoreFaces = payload.restoreFaces,
        sampler = payload.sampler,
        seed = payload.seed,
        subSeed = payload.subSeed,
        subSeedStrength = payload.subSeedStrength,
        hidden = false,
    )
}

/**
 * Converts SDAI data with `mapToStabilityPrompt`.
 *
 * @param defaultWeight default weight value consumed by the API.
 * @return Result produced by `mapToStabilityPrompt`.
 * @author Dmitriy Moroz
 */
fun String.mapToStabilityPrompt(defaultWeight: Double = 1.0): List<StabilityTextPromptRaw> =
    buildList {
        this@mapToStabilityPrompt
            .split(',')
            .map(String::trim)
            .filter(String::isNotBlank)
            .map {
                if (it.startsWith("(") && it.endsWith(")") && it.split(":").size == 2) {
                    val value = it.replace("(", "").replace(")", "").split(":")
                    add(
                        StabilityTextPromptRaw(
                            text = value.firstOrNull() ?: "",
                            weight = value.lastOrNull()?.toDoubleOrNull() ?: defaultWeight,
                        )
                    )
                } else {
                    add(StabilityTextPromptRaw(it, defaultWeight))
                }
            }
    }

/**
 * Executes the `decodeBase64ImageBytes` step in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalEncodingApi::class)
fun String.decodeBase64ImageBytes(): ByteArray = substringAfter("base64,", this)
    .filterNot(Char::isWhitespace)
    .let(Base64.Default::decode)
