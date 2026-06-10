package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `HuggingFaceGenerationRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class HuggingFaceGenerationRequest(
    /**
     * Exposes the `inputs` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("inputs")
    val inputs: String,
    /**
     * Exposes the `parameters` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("parameters")
    val parameters: Parameters = Parameters(),
) {
    @Serializable
    data class Parameters(
        @SerialName("width")
        val width: Int? = null,
        @SerialName("height")
        val height: Int? = null,
        @SerialName("text")
        val text: String? = null,
        @SerialName("negative_prompt")
        val negativePrompt: String? = null,
        @SerialName("seed")
        val seed: String? = null,
        @SerialName("num_inference_steps")
        val numInferenceSteps: Int? = null,
        @SerialName("guidance_scale")
        val guidanceScale: Float? = null,
    )
}
