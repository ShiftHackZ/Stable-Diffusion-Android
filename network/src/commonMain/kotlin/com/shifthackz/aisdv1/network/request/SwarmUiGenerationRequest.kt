package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `SwarmUiGenerationRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class SwarmUiGenerationRequest(
    /**
     * Exposes the `sessionId` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("session_id")
    val sessionId: String,
    /**
     * Exposes the `model` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("model")
    val model: String,
    /**
     * Exposes the `initImage` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("initimage")
    val initImage: String?,
    /**
     * Exposes the `initImageCreativity` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("initimagecreativity")
    val initImageCreativity: String?,
    /**
     * Exposes the `images` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("images")
    val images: Int,
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
    @SerialName("negativeprompt")
    val negativePrompt: String,
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
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: String?,
    /**
     * Exposes the `variationSeed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("variationseed")
    val variationSeed: String?,
    /**
     * Exposes the `variationSeedStrength` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("variationseedstrength")
    val variationSeedStrength: String?,
    /**
     * Exposes the `cfgScale` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("cfgscale")
    val cfgScale: Float?,
    /**
     * Exposes the `steps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("steps")
    val steps: Int,
)
