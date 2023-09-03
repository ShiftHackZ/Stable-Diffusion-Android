package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class HordeGenerationCheckResponse(
    @SerializedName("done")
    val done: Boolean?,
    @SerializedName("is_possible")
    val isPossible: Boolean?,
    @SerializedName("wait_time")
    val waitTime: Int?,
    @SerializedName("queue_position")
    val queuePosition: Int?,
)

data class HordeGenerationCheckFullResponse(
    @SerializedName("done")
    val done: Boolean?,
    @SerializedName("is_possible")
    val isPossible: Boolean?,
    @SerializedName("wait_time")
    val waitTime: Int?,
    @SerializedName("queue_position")
    val queuePosition: Int?,
    @SerializedName("generations")
    val generations: List<Generation>?,
) {
    data class Generation(
        @SerializedName("id")
        val id: String?,
        @SerializedName("img")
        val img: String?,
        @SerializedName("seed")
        val seed: String?,
        @SerializedName("censored")
        val censored: Boolean?,
    )
}
