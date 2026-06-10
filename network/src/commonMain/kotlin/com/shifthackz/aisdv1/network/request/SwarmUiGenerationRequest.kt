package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SwarmUiGenerationRequest(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("model")
    val model: String,
    @SerialName("initimage")
    val initImage: String?,
    @SerialName("initimagecreativity")
    val initImageCreativity: String?,
    @SerialName("images")
    val images: Int,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("negativeprompt")
    val negativePrompt: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("seed")
    val seed: String?,
    @SerialName("variationseed")
    val variationSeed: String?,
    @SerialName("variationseedstrength")
    val variationSeedStrength: String?,
    @SerialName("cfgscale")
    val cfgScale: Float?,
    @SerialName("steps")
    val steps: Int,
)
