package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `HordeGenerationCheckResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class HordeGenerationCheckResponse(
    /**
     * Exposes the `done` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("done")
    val done: Boolean? = null,
    /**
     * Exposes the `isPossible` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("is_possible")
    val isPossible: Boolean? = null,
    /**
     * Exposes the `waitTime` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("wait_time")
    val waitTime: Int? = null,
    /**
     * Exposes the `queuePosition` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("queue_position")
    val queuePosition: Int? = null,
)

/**
 * Carries `HordeGenerationCheckFullResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class HordeGenerationCheckFullResponse(
    /**
     * Exposes the `done` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("done")
    val done: Boolean? = null,
    /**
     * Exposes the `isPossible` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("is_possible")
    val isPossible: Boolean? = null,
    /**
     * Exposes the `waitTime` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("wait_time")
    val waitTime: Int? = null,
    /**
     * Exposes the `queuePosition` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("queue_position")
    val queuePosition: Int? = null,
    /**
     * Exposes the `generations` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
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
