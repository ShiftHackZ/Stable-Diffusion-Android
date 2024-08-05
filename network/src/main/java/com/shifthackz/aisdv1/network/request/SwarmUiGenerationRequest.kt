package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class SwarmUiGenerationRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("initimage")
    val initImage: String?,
    @SerializedName("initimagecreativity")
    val initImageCreativity: String?,
    @SerializedName("images")
    val images: Int,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("negativeprompt")
    val negativePrompt: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("seed")
    val seed: String?,
    @SerializedName("variationseed")
    val variationSeed: String?,
    @SerializedName("variationseedstrength")
    val variationSeedStrength: String?,
    @SerializedName("cfgscale")
    val cfgScale: Float?,
    @SerializedName("steps")
    val steps: Int,
//    @SerializedName("initimageresettonorm")
//    val initimageresettonorm: String = "0",
//    @SerializedName("initimagerecompositemask")
//    val initimagerecompositemask: String = "0",

)
