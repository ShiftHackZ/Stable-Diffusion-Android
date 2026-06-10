package com.shifthackz.aisdv1.network.request

import com.shifthackz.aisdv1.network.model.StabilityTextPromptRaw
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StabilityTextToImageRequest(
    @SerialName("height")
    val height: Int,
    @SerialName("width")
    val width: Int,
    @SerialName("text_prompts")
    val textPrompts: List<StabilityTextPromptRaw>,
    @SerialName("cfg_scale")
    val cfgScale: Float,
    @SerialName("clip_guidance_preset")
    val clipGuidancePreset: String,
    @SerialName("sampler")
    val sampler: String?,
    @SerialName("seed")
    val seed: Long,
    @SerialName("steps")
    val steps: Int,
    @SerialName("style_preset")
    val stylePreset: String?,
)
