package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries A1111 script-info metadata through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class StableDiffusionScriptInfoRaw(
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("name")
    val name: String = "",
    /**
     * Exposes the `isAlwaysOn` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("is_alwayson")
    val isAlwaysOn: Boolean = false,
    /**
     * Exposes the `isImg2Img` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("is_img2img")
    val isImg2Img: Boolean = false,
)
