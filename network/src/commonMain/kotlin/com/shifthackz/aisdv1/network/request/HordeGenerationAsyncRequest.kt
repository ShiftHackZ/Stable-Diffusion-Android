package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `HordeGenerationAsyncRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class HordeGenerationAsyncRequest(
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String,
    /**
     * Exposes the `params` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("params")
    val params: Params,
    /**
     * Exposes the `nsfw` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("nsfw")
    val nsfw: Boolean,
    /**
     * Exposes the `sourceProcessing` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("source_processing")
    val sourceProcessing: String?,
    /**
     * Exposes the `sourceImage` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("source_image")
    val sourceImage: String?,
) {
    /**
     * Carries `Params` data through the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @Serializable
    data class Params(
        /**
         * Exposes the `cfgScale` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        @SerialName("cfg_scale")
        val cfgScale: Float,
        /**
         * Exposes the `width` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        @SerialName("width")
        val width: Int,
        /**
         * Exposes the `height` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        @SerialName("height")
        val height: Int,
        /**
         * Exposes the `steps` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        @SerialName("steps")
        val steps: Int?,
        /**
         * Exposes the `seed` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        @SerialName("seed")
        val seed: String?,
        /**
         * Exposes the `subSeedStrength` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        @SerialName("denoising_strength")
        val subSeedStrength: Float?,
    )
}
