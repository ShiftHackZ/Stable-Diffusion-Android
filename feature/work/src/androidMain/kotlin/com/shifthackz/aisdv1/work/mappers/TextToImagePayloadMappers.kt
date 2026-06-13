package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.FalAiAcceleration
import com.shifthackz.aisdv1.domain.entity.FalAiImageSize
import com.shifthackz.aisdv1.domain.entity.FalAiModel
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Exposes the `payloadJson` value used by the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalSerializationApi::class)
internal val payloadJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

/**
 * Converts SDAI data with `toByteArray`.
 *
 * @return Result produced by `toByteArray`.
 * @author Dmitriy Moroz
 */
internal fun TextToImagePayload.toByteArray(): ByteArray {
    return payloadJson
        .encodeToString(TextToImagePayloadDto.from(this))
        .encodeToByteArray()
}

/**
 * Converts SDAI data with `toTextToImagePayload`.
 *
 * @return Result produced by `toTextToImagePayload`.
 * @author Dmitriy Moroz
 */
internal fun ByteArray.toTextToImagePayload(): TextToImagePayload? {
    return runCatching {
        payloadJson
            .decodeFromString<TextToImagePayloadDto>(decodeToString())
            .toPayload()
    }.getOrNull()
}

/**
 * Carries `TextToImagePayloadDto` data through the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
private data class TextToImagePayloadDto(
    /**
     * Exposes the `prompt` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String,
    /**
     * Exposes the `samplingSteps` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val samplingSteps: Int,
    /**
     * Exposes the `cfgScale` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val cfgScale: Float,
    /**
     * Exposes the `width` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val height: Int,
    /**
     * Exposes the `restoreFaces` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val restoreFaces: Boolean,
    /**
     * Exposes the `seed` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val seed: String,
    /**
     * Exposes the `subSeed` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeed: String,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeedStrength: Float,
    /**
     * Exposes the `sampler` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val sampler: String,
    /**
     * Exposes the `scheduler` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val scheduler: String? = null,
    /**
     * Exposes the `nsfw` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val nsfw: Boolean,
    /**
     * Exposes the `batchCount` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val batchCount: Int,
    /**
     * Exposes the `style` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val style: String?,
    /**
     * Exposes the `quality` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val quality: String?,
    /**
     * Exposes the `openAiModel` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiModel: String?,
    /**
     * Exposes the `stabilityAiClipGuidance` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiClipGuidance: String?,
    /**
     * Exposes the `stabilityAiStylePreset` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiStylePreset: String?,
    /**
     * Exposes the `aDetailer` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val aDetailer: ADetailerConfigDto = ADetailerConfigDto(),
    /**
     * Exposes the `hires` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val hires: HiresConfigDto = HiresConfigDto(),
    /**
     * Exposes the `forgeModules` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val forgeModules: List<ForgeModuleDto> = emptyList(),
    /**
     * Exposes the `falAiModel` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiModel: String? = null,
    /**
     * Exposes the `falAiImageSize` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiImageSize: String? = null,
    /**
     * Exposes the `falAiAcceleration` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiAcceleration: String? = null,
    /**
     * Exposes the `sdxlBackend` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val sdxlBackend: String? = null,
    /**
     * Exposes the `falAiSyncMode` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiSyncMode: Boolean = false,
) {
    /**
     * Converts SDAI data with `toPayload`.
     *
     * @author Dmitriy Moroz
     */
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
        scheduler = scheduler?.let(Scheduler::fromAlias) ?: Scheduler.AUTOMATIC,
        nsfw = nsfw,
        batchCount = batchCount,
        style = style,
        quality = quality,
        openAiModel = openAiModel?.let(OpenAiModel::parse),
        stabilityAiClipGuidance = stabilityAiClipGuidance.parseEnumOrNull<StabilityAiClipGuidance>(),
        stabilityAiStylePreset = stabilityAiStylePreset.parseEnumOrNull<StabilityAiStylePreset>(),
        aDetailer = aDetailer.toDomain(),
        hires = hires.toDomain(),
        forgeModules = forgeModules.map(ForgeModuleDto::toDomain),
        falAiModel = FalAiModel.parse(falAiModel, FalAiModel.defaultTextToImage),
        falAiImageSize = FalAiImageSize.parse(falAiImageSize),
        falAiAcceleration = FalAiAcceleration.parse(falAiAcceleration),
        sdxlBackend = SdxlBackend.parse(sdxlBackend),
        falAiSyncMode = falAiSyncMode,
    )

    /**
     * Provides the `companion object` singleton used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `from` step in the SDAI background work feature layer.
         *
         * @param payload generation payload used by the operation.
         * @author Dmitriy Moroz
         */
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
            scheduler = payload.scheduler.alias,
            nsfw = payload.nsfw,
            batchCount = payload.batchCount,
            style = payload.style,
            quality = payload.quality,
            openAiModel = payload.openAiModel?.alias,
            stabilityAiClipGuidance = payload.stabilityAiClipGuidance?.name,
            stabilityAiStylePreset = payload.stabilityAiStylePreset?.name,
            aDetailer = ADetailerConfigDto.from(payload.aDetailer),
            hires = HiresConfigDto.from(payload.hires),
            forgeModules = payload.forgeModules.map(ForgeModuleDto::from),
            falAiModel = payload.falAiModel.alias,
            falAiImageSize = payload.falAiImageSize.key,
            falAiAcceleration = payload.falAiAcceleration.key,
            sdxlBackend = payload.sdxlBackend.key,
            falAiSyncMode = payload.falAiSyncMode,
        )
    }
}

/**
 * Executes the `function` step in the SDAI background work feature layer.
 *
 * @return Result produced by `function`.
 * @author Dmitriy Moroz
 */
internal inline fun <reified T : Enum<T>> String?.parseEnumOrNull(): T? {
    return this?.let { value -> enumValues<T>().firstOrNull { it.name == value } }
}
