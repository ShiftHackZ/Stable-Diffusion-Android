package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Carries `ImageToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class ImageToImageRequest(
    /**
     * Exposes the `initImages` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("init_images")
    val initImages: List<String>,
    /**
     * Exposes the `includeInitImages` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("include_init_images")
    val includeInitImages: Boolean,
    /**
     * Exposes the `mask` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("mask")
    val mask: String?,
    /**
     * Exposes the `inPaintingMaskInvert` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("inpainting_mask_invert")
    val inPaintingMaskInvert: Int?,
    /**
     * Exposes the `inPaintFullResPadding` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("inpaint_full_res_padding")
    val inPaintFullResPadding: Int?,
    /**
     * Exposes the `inPaintingFill` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("inpainting_fill")
    val inPaintingFill: Int?,
    /**
     * Exposes the `inPaintFullRes` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("inpaint_full_res")
    val inPaintFullRes: Boolean?,
    /**
     * Exposes the `maskBlur` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("mask_blur")
    val maskBlur: Int?,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("denoising_strength")
    val denoisingStrength: Float,
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
    /**
     * Exposes the `scheduler` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("scheduler")
    val scheduler: String? = null,
    /**
     * Exposes the `alwaysOnScripts` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("alwayson_scripts")
    val alwaysOnScripts: JsonObject? = null,
)
