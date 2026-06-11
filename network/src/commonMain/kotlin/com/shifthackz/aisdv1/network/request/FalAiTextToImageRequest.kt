package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `FalAiTextToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class FalAiTextToImageRequest(
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String,
    /**
     * Exposes the `imageSize` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("image_size")
    val imageSize: String,
    /**
     * Exposes the `numInferenceSteps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("num_inference_steps")
    val numInferenceSteps: Int,
    /**
     * Exposes the `guidanceScale` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("guidance_scale")
    val guidanceScale: Float,
    /**
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: Long? = null,
    /**
     * Exposes the `syncMode` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sync_mode")
    val syncMode: Boolean = false,
    /**
     * Exposes the `numImages` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("num_images")
    val numImages: Int = 1,
    /**
     * Exposes the `enableSafetyChecker` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("enable_safety_checker")
    val enableSafetyChecker: Boolean = true,
    /**
     * Exposes the `outputFormat` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("output_format")
    val outputFormat: String = "png",
    /**
     * Exposes the `acceleration` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("acceleration")
    val acceleration: String = "none",
)

/**
 * Carries `FalAiImageToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class FalAiImageToImageRequest(
    /**
     * Exposes the `imageUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("image_url")
    val imageUrl: String,
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String,
    /**
     * Exposes the `strength` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("strength")
    val strength: Float,
    /**
     * Exposes the `imageSize` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("image_size")
    val imageSize: String? = null,
    /**
     * Exposes the `numInferenceSteps` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("num_inference_steps")
    val numInferenceSteps: Int,
    /**
     * Exposes the `guidanceScale` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("guidance_scale")
    val guidanceScale: Float,
    /**
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: Long? = null,
    /**
     * Exposes the `syncMode` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sync_mode")
    val syncMode: Boolean = false,
    /**
     * Exposes the `numImages` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("num_images")
    val numImages: Int = 1,
    /**
     * Exposes the `enableSafetyChecker` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("enable_safety_checker")
    val enableSafetyChecker: Boolean = true,
    /**
     * Exposes the `outputFormat` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("output_format")
    val outputFormat: String = "png",
    /**
     * Exposes the `acceleration` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("acceleration")
    val acceleration: String = "none",
)
