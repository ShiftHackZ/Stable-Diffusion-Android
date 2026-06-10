package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `TextToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class TextToImageRequest(
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("negative_prompt")
    val negativePrompt: String,
    /**
     * Exposes the `steps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("steps")
    val steps: Int,
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
     * Exposes the `batchSize` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("batch_size")
    val batchSize: Int,
    /**
     * Exposes the `restoreFaces` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("restore_faces")
    val restoreFaces: Boolean,
    /**
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: String?,
    /**
     * Exposes the `subSeed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("subseed")
    val subSeed: String?,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("subseed_strength")
    val subSeedStrength: Float?,
    /**
     * Exposes the `samplerIndex` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sampler_index")
    val samplerIndex: String,
)
