package com.shifthackz.aisdv1.network.request

import com.shifthackz.aisdv1.network.model.StabilityTextPromptRaw
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `StabilityTextToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StabilityTextToImageRequest(
    /**
     * Exposes the `height` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("height")
    val height: Int,
    /**
     * Exposes the `width` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("width")
    val width: Int,
    /**
     * Exposes the `textPrompts` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("text_prompts")
    val textPrompts: List<StabilityTextPromptRaw>,
    /**
     * Exposes the `cfgScale` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("cfg_scale")
    val cfgScale: Float,
    /**
     * Exposes the `clipGuidancePreset` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("clip_guidance_preset")
    val clipGuidancePreset: String,
    /**
     * Exposes the `sampler` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sampler")
    val sampler: String?,
    /**
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: Long,
    /**
     * Exposes the `steps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("steps")
    val steps: Int,
    /**
     * Exposes the `stylePreset` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("style_preset")
    val stylePreset: String?,
)
