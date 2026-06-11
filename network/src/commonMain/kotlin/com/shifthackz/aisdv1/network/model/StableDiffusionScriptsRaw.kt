package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `StableDiffusionScriptsRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StableDiffusionScriptsRaw(
    /**
     * Exposes the `txt2img` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("txt2img")
    val txt2img: List<String> = emptyList(),
    /**
     * Exposes the `img2img` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("img2img")
    val img2img: List<String> = emptyList(),
)
