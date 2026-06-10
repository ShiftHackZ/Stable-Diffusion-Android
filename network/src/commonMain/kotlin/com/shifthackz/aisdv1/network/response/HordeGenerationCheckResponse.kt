package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HordeGenerationCheckResponse(
    @SerialName("done")
    val done: Boolean? = null,
    @SerialName("is_possible")
    val isPossible: Boolean? = null,
    @SerialName("wait_time")
    val waitTime: Int? = null,
    @SerialName("queue_position")
    val queuePosition: Int? = null,
)

@Serializable
data class HordeGenerationCheckFullResponse(
    @SerialName("done")
    val done: Boolean? = null,
    @SerialName("is_possible")
    val isPossible: Boolean? = null,
    @SerialName("wait_time")
    val waitTime: Int? = null,
    @SerialName("queue_position")
    val queuePosition: Int? = null,
    @SerialName("generations")
    val generations: List<Generation>? = null,
) {
    @Serializable
    data class Generation(
        @SerialName("id")
        val id: String? = null,
        @SerialName("img")
        val img: String? = null,
        @SerialName("seed")
        val seed: String? = null,
        @SerialName("censored")
        val censored: Boolean? = null,
    )
}
