package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.OverrideSettings
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Converts SDAI data with `mapToStableDiffusionRequest`.
 *
 * @author Dmitriy Moroz
 */
fun TextToImagePayload.mapToStableDiffusionRequest(): TextToImageRequest = with(this) {
    TextToImageRequest(
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        batchSize = batchCount,
        restoreFaces = restoreFaces,
        seed = seed.trim().ifEmpty { null },
        subSeed = subSeed.trim().ifEmpty { null },
        subSeedStrength = subSeedStrength,
        samplerIndex = sampler,
        scheduler = scheduler.mapToRequestScheduler(),
        alwaysOnScripts = aDetailer.mapToAlwaysOnScripts(),
        enableHr = true.takeIf { hires.enabled },
        hrUpscaler = hires.upscaler.takeIf { hires.enabled },
        hrScale = hires.scale.takeIf { hires.enabled },
        hrSecondPassSteps = hires.steps.takeIf { hires.enabled },
        hrCfg = hires.hrCfg.takeIf { hires.enabled },
        hrDistilledCfg = hires.hrDistilledCfg.takeIf { hires.enabled },
        denoisingStrength = hires.denoisingStrength.takeIf { hires.enabled },
        overrideSettings = forgeModules
            .map { it.path }
            .takeIf(List<String>::isNotEmpty)
            ?.let(::OverrideSettings),
    )
}

/**
 * Converts SDAI data with `mapToStableDiffusionRequest`.
 *
 * @author Dmitriy Moroz
 */
fun ImageToImagePayload.mapToStableDiffusionRequest(): ImageToImageRequest = with(this) {
    ImageToImageRequest(
        initImages = listOf(base64Image),
        includeInitImages = true,
        mask = base64MaskImage.takeIf(String::isNotBlank),
        inPaintingMaskInvert = inPaintingMaskInvert,
        inPaintFullResPadding = inPaintFullResPadding,
        inPaintingFill = inPaintingFill,
        inPaintFullRes = inPaintFullRes,
        maskBlur = maskBlur,
        denoisingStrength = denoisingStrength,
        prompt = prompt,
        negativePrompt = negativePrompt,
        steps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        batchSize = batchCount,
        restoreFaces = restoreFaces,
        seed = seed.trim().ifEmpty { null },
        subSeed = subSeed.trim().ifEmpty { null },
        subSeedStrength = subSeedStrength,
        samplerIndex = sampler,
        scheduler = scheduler.mapToRequestScheduler(),
        alwaysOnScripts = aDetailer.mapToAlwaysOnScripts(),
    )
}

/**
 * Converts SDAI data with `mapToRequestScheduler`.
 *
 * @return Result produced by `mapToRequestScheduler`.
 * @author Dmitriy Moroz
 */
fun Scheduler.mapToRequestScheduler(): String? =
    alias.takeIf { this != Scheduler.AUTOMATIC }

/**
 * Converts SDAI data with `mapToAlwaysOnScripts`.
 *
 * @return Result produced by `mapToAlwaysOnScripts`.
 * @author Dmitriy Moroz
 */
fun ADetailerConfig.mapToAlwaysOnScripts(): JsonObject? {
    if (!enabled) return null
    return buildJsonObject {
        putJsonObject("ADetailer") {
            putJsonArray("args") {
                add(true)
                add(false)
                add(
                    buildJsonObject {
                        put("ad_model", model)
                        put("ad_prompt", prompt)
                        put("ad_negative_prompt", negativePrompt)
                        put("ad_confidence", JsonPrimitive(confidence))
                        put("ad_dilate_erode", maskBlur)
                        put("ad_denoising_strength", JsonPrimitive(denoisingStrength))
                        put("ad_inpaint_only_masked", inpaintOnlyMasked)
                        put("ad_inpaint_only_masked_padding", inpaintPadding)
                        put("is_api", true)
                    }
                )
            }
        }
    }
}

/**
 * Converts SDAI data with `mapStableDiffusionTextToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapStableDiffusionTextToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<TextToImagePayload, SdGenerationResponse>.mapStableDiffusionTextToImageResult(
    createdAtMillis: Long,
): List<AiGenerationResult> {
    val (payload, response) = this
    val info = parseInfo(response.info)
    return response.images.orEmpty().mapIndexed { index, image ->
        AiGenerationResult(
            id = 0L,
            image = image,
            inputImage = "",
            createdAt = createdAtMillis + index,
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
            seed = payload.seed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSeedFromRemote(info, index),
            subSeed = payload.subSeed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSubSeedFromRemote(info, index),
            subSeedStrength = payload.subSeedStrength,
            hidden = false,
        )
    }
}

/**
 * Converts SDAI data with `mapStableDiffusionImageToImageResult`.
 *
 * @param createdAtMillis creation timestamp in milliseconds.
 * @return Result produced by `mapStableDiffusionImageToImageResult`.
 * @author Dmitriy Moroz
 */
fun Pair<ImageToImagePayload, SdGenerationResponse>.mapStableDiffusionImageToImageResult(
    createdAtMillis: Long,
): List<AiGenerationResult> {
    val (payload, response) = this
    val info = parseInfo(response.info)
    return response.images.orEmpty().mapIndexed { index, image ->
        AiGenerationResult(
            id = 0L,
            image = image,
            inputImage = payload.base64Image,
            createdAt = createdAtMillis + index,
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
            seed = payload.seed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSeedFromRemote(info, index),
            subSeed = payload.subSeed.takeIf { it.trim().isNotEmpty() }
                ?: mapStableDiffusionSubSeedFromRemote(info, index),
            subSeedStrength = payload.subSeedStrength,
            hidden = false,
        )
    }
}

/**
 * Converts SDAI data with `mapStableDiffusionSeedFromRemote`.
 *
 * @param info info value consumed by the API.
 * @param index index value consumed by the API.
 * @return Result produced by `mapStableDiffusionSeedFromRemote`.
 * @author Dmitriy Moroz
 */
private fun mapStableDiffusionSeedFromRemote(info: SdGenerationResponse.Info?, index: Int): String =
    info?.allSeeds?.getOrNull(index)?.toString()
        ?: info?.seed?.toString()
        ?: ""

/**
 * Converts SDAI data with `mapStableDiffusionSubSeedFromRemote`.
 *
 * @param info info value consumed by the API.
 * @param index index value consumed by the API.
 * @return Result produced by `mapStableDiffusionSubSeedFromRemote`.
 * @author Dmitriy Moroz
 */
private fun mapStableDiffusionSubSeedFromRemote(info: SdGenerationResponse.Info?, index: Int): String =
    info?.allSubSeeds?.getOrNull(index)?.toString()
        ?: info?.subSeed?.toString()
        ?: ""

/**
 * Executes the `parseInfo` step in the SDAI data layer.
 *
 * @param infoString info string value consumed by the API.
 * @return Result produced by `parseInfo`.
 * @author Dmitriy Moroz
 */
private fun parseInfo(infoString: String?): SdGenerationResponse.Info? = infoString
    ?.takeIf(String::isNotBlank)
    ?.let { raw ->
        runCatching {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }.decodeFromString<SdGenerationResponse.Info>(raw)
        }.getOrNull()
    }
