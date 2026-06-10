package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
internal val payloadJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal fun TextToImagePayload.toByteArray(): ByteArray {
    return payloadJson
        .encodeToString(TextToImagePayloadDto.from(this))
        .encodeToByteArray()
}

internal fun ByteArray.toTextToImagePayload(): TextToImagePayload? {
    return runCatching {
        payloadJson
            .decodeFromString<TextToImagePayloadDto>(decodeToString())
            .toPayload()
    }.getOrNull()
}

@Serializable
private data class TextToImagePayloadDto(
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
    val cfgScale: Float,
    val width: Int,
    val height: Int,
    val restoreFaces: Boolean,
    val seed: String,
    val subSeed: String,
    val subSeedStrength: Float,
    val sampler: String,
    val nsfw: Boolean,
    val batchCount: Int,
    val style: String?,
    val quality: String?,
    val openAiModel: String?,
    val stabilityAiClipGuidance: String?,
    val stabilityAiStylePreset: String?,
) {
    fun toPayload(): TextToImagePayload = TextToImagePayload(
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        restoreFaces = restoreFaces,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength,
        sampler = sampler,
        nsfw = nsfw,
        batchCount = batchCount,
        style = style,
        quality = quality,
        openAiModel = openAiModel?.let(OpenAiModel::parse),
        stabilityAiClipGuidance = stabilityAiClipGuidance.parseEnumOrNull<StabilityAiClipGuidance>(),
        stabilityAiStylePreset = stabilityAiStylePreset.parseEnumOrNull<StabilityAiStylePreset>(),
    )

    companion object {
        fun from(payload: TextToImagePayload): TextToImagePayloadDto = TextToImagePayloadDto(
            prompt = payload.prompt,
            negativePrompt = payload.negativePrompt,
            samplingSteps = payload.samplingSteps,
            cfgScale = payload.cfgScale,
            width = payload.width,
            height = payload.height,
            restoreFaces = payload.restoreFaces,
            seed = payload.seed,
            subSeed = payload.subSeed,
            subSeedStrength = payload.subSeedStrength,
            sampler = payload.sampler,
            nsfw = payload.nsfw,
            batchCount = payload.batchCount,
            style = payload.style,
            quality = payload.quality,
            openAiModel = payload.openAiModel?.alias,
            stabilityAiClipGuidance = payload.stabilityAiClipGuidance?.name,
            stabilityAiStylePreset = payload.stabilityAiStylePreset?.name,
        )
    }
}

internal inline fun <reified T : Enum<T>> String?.parseEnumOrNull(): T? {
    return this?.let { value -> enumValues<T>().firstOrNull { it.name == value } }
}
