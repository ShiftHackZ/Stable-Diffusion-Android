package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Carries `TextToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class TextToImageRequest(
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("negative_prompt")
    val negativePrompt: String,
    /**
     * Exposes the `steps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("steps")
    val steps: Int,
    /**
     * Exposes the `cfgScale` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("cfg_scale")
    val cfgScale: Float,
    /**
     * Exposes the `width` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("width")
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("height")
    val height: Int,
    /**
     * Exposes the `batchSize` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("batch_size")
    val batchSize: Int,
    /**
     * Exposes the `restoreFaces` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("restore_faces")
    val restoreFaces: Boolean,
    /**
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: String?,
    /**
     * Exposes the `subSeed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("subseed")
    val subSeed: String?,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("subseed_strength")
    val subSeedStrength: Float?,
    /**
     * Exposes the `samplerIndex` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sampler_index")
    val samplerIndex: String,
    /**
     * Exposes the `scheduler` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("scheduler")
    val scheduler: String? = null,
    /**
     * Exposes the `alwaysOnScripts` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("alwayson_scripts")
    val alwaysOnScripts: JsonObject? = null,
    /**
     * Exposes the `enableHr` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("enable_hr")
    val enableHr: Boolean? = null,
    /**
     * Exposes the `hrUpscaler` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("hr_upscaler")
    val hrUpscaler: String? = null,
    /**
     * Exposes the `hrScale` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("hr_scale")
    val hrScale: Float? = null,
    /**
     * Exposes the `hrSecondPassSteps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("hr_second_pass_steps")
    val hrSecondPassSteps: Int? = null,
    /**
     * Exposes the `hrCfg` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("hr_cfg")
    val hrCfg: Float? = null,
    /**
     * Exposes the `hrDistilledCfg` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("hr_distilled_cfg")
    val hrDistilledCfg: Float? = null,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("denoising_strength")
    val denoisingStrength: Float? = null,
    /**
     * Exposes the `overrideSettings` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("override_settings")
    val overrideSettings: OverrideSettings? = null,
)
