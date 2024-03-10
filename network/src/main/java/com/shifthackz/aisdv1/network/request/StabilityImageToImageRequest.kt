package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName
import com.shifthackz.aisdv1.network.model.StabilityTextPromptRaw

data class StabilityImageToImageRequest(
    @SerializedName("text_prompts")
    val textPrompts: List<StabilityTextPromptRaw>,
    @SerializedName("init_image")
    val initImage: String,
    @SerializedName("init_image_mode")
    val initImageMode: String,
    @SerializedName("image_strength")
    val imageStrength: Float,
    @SerializedName("cfg_scale")
    val cfgScale: Float,
    @SerializedName("clip_guidance_preset")
    val clipGuidancePreset: String,
    @SerializedName("sampler")
    val sampler: String,
    @SerializedName("seed")
    val seed: Long,
    @SerializedName("steps")
    val steps: Int,
    @SerializedName("style_preset")
    val stylePreset: String?,
)
