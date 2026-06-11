package com.shifthackz.aisdv1.work.mappers

import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import kotlinx.serialization.Serializable

/**
 * Carries `ADetailerConfigDto` data through the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
internal data class ADetailerConfigDto(
    /**
     * Exposes the `enabled` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val enabled: Boolean = false,
    /**
     * Exposes the `model` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val model: String = "face_yolov8s.pt",
    /**
     * Exposes the `prompt` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String = "",
    /**
     * Exposes the `negativePrompt` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String = "",
    /**
     * Exposes the `confidence` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val confidence: Float = 0.3f,
    /**
     * Exposes the `maskBlur` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val maskBlur: Int = 4,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: Float = 0.4f,
    /**
     * Exposes the `inpaintOnlyMasked` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val inpaintOnlyMasked: Boolean = true,
    /**
     * Exposes the `inpaintPadding` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val inpaintPadding: Int = 32,
) {

    /**
     * Converts SDAI data with `toDomain`.
     *
     * @return Result produced by `toDomain`.
     * @author Dmitriy Moroz
     */
    fun toDomain() = ADetailerConfig(
        enabled = enabled,
        model = model,
        prompt = prompt,
        negativePrompt = negativePrompt,
        confidence = confidence,
        maskBlur = maskBlur,
        denoisingStrength = denoisingStrength,
        inpaintOnlyMasked = inpaintOnlyMasked,
        inpaintPadding = inpaintPadding,
    )

    companion object {
        /**
         * Converts SDAI data with `from`.
         *
         * @param config config value consumed by the API.
         * @return Result produced by `from`.
         * @author Dmitriy Moroz
         */
        fun from(config: ADetailerConfig) = ADetailerConfigDto(
            enabled = config.enabled,
            model = config.model,
            prompt = config.prompt,
            negativePrompt = config.negativePrompt,
            confidence = config.confidence,
            maskBlur = config.maskBlur,
            denoisingStrength = config.denoisingStrength,
            inpaintOnlyMasked = config.inpaintOnlyMasked,
            inpaintPadding = config.inpaintPadding,
        )
    }
}

/**
 * Carries `HiresConfigDto` data through the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
internal data class HiresConfigDto(
    /**
     * Exposes the `enabled` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val enabled: Boolean = false,
    /**
     * Exposes the `upscaler` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val upscaler: String = "None",
    /**
     * Exposes the `scale` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val scale: Float = 2f,
    /**
     * Exposes the `steps` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val steps: Int = 0,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: Float = 0.4f,
    /**
     * Exposes the `hrCfg` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val hrCfg: Float? = null,
    /**
     * Exposes the `hrDistilledCfg` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val hrDistilledCfg: Float? = null,
) {

    /**
     * Converts SDAI data with `toDomain`.
     *
     * @return Result produced by `toDomain`.
     * @author Dmitriy Moroz
     */
    fun toDomain() = HiresConfig(
        enabled = enabled,
        upscaler = upscaler,
        scale = scale,
        steps = steps,
        denoisingStrength = denoisingStrength,
        hrCfg = hrCfg,
        hrDistilledCfg = hrDistilledCfg,
    )

    companion object {
        /**
         * Converts SDAI data with `from`.
         *
         * @param config config value consumed by the API.
         * @return Result produced by `from`.
         * @author Dmitriy Moroz
         */
        fun from(config: HiresConfig) = HiresConfigDto(
            enabled = config.enabled,
            upscaler = config.upscaler,
            scale = config.scale,
            steps = config.steps,
            denoisingStrength = config.denoisingStrength,
            hrCfg = config.hrCfg,
            hrDistilledCfg = config.hrDistilledCfg,
        )
    }
}

/**
 * Carries `ForgeModuleDto` data through the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
internal data class ForgeModuleDto(
    /**
     * Exposes the `name` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String = "",
    /**
     * Exposes the `path` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    val path: String = "",
) {

    /**
     * Converts SDAI data with `toDomain`.
     *
     * @return Result produced by `toDomain`.
     * @author Dmitriy Moroz
     */
    fun toDomain() = ForgeModule(
        name = name,
        path = path,
    )

    companion object {
        /**
         * Converts SDAI data with `from`.
         *
         * @param module module value consumed by the API.
         * @return Result produced by `from`.
         * @author Dmitriy Moroz
         */
        fun from(module: ForgeModule) = ForgeModuleDto(
            name = module.name,
            path = module.path,
        )
    }
}
