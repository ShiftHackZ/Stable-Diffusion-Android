package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `StabilityTextPromptRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StabilityTextPromptRaw(
    /**
     * Exposes the `text` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("text")
    val text: String,
    /**
     * Exposes the `weight` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("weight")
    val weight: Double,
)
