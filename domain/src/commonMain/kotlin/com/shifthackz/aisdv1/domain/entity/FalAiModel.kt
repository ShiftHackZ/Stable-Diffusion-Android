package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates supported Fal.ai model endpoints in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class FalAiModel(
    /**
     * Exposes the `alias` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val alias: String,
    /**
     * Exposes the `displayName` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val displayName: String,
    /**
     * Exposes the `generationMode` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val generationMode: FalAiGenerationMode,
    /**
     * Exposes the `minInferenceSteps` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val minInferenceSteps: Int,
    /**
     * Exposes the `maxInferenceSteps` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val maxInferenceSteps: Int,
    /**
     * Exposes the `minGuidanceScale` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val minGuidanceScale: Float,
    /**
     * Exposes the `maxGuidanceScale` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val maxGuidanceScale: Float,
    /**
     * Exposes the `supportsImageSize` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val supportsImageSize: Boolean = true,
    /**
     * Exposes the `supportedAccelerations` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val supportedAccelerations: Set<FalAiAcceleration> = FalAiAcceleration.entries.toSet(),
) {
    FLUX_SCHNELL(
        alias = "fal-ai/flux/schnell",
        displayName = "flux/schnell (Text to Image)",
        generationMode = FalAiGenerationMode.TEXT_TO_IMAGE,
        minInferenceSteps = 1,
        maxInferenceSteps = 12,
        minGuidanceScale = 1f,
        maxGuidanceScale = 20f,
    ),
    FLUX_DEV(
        alias = "fal-ai/flux/dev",
        displayName = "flux/dev (Text to Image)",
        generationMode = FalAiGenerationMode.TEXT_TO_IMAGE,
        minInferenceSteps = 1,
        maxInferenceSteps = 50,
        minGuidanceScale = 1f,
        maxGuidanceScale = 20f,
    ),
    FLUX_DEV_IMAGE_TO_IMAGE(
        alias = "fal-ai/flux/dev/image-to-image",
        displayName = "flux/dev/image-to-image (Image to Image)",
        generationMode = FalAiGenerationMode.IMAGE_TO_IMAGE,
        minInferenceSteps = 10,
        maxInferenceSteps = 50,
        minGuidanceScale = 1f,
        maxGuidanceScale = 20f,
        supportsImageSize = false,
    ),
    FLUX_LORA(
        alias = "fal-ai/flux-lora",
        displayName = "flux-lora (Text to Image)",
        generationMode = FalAiGenerationMode.TEXT_TO_IMAGE,
        minInferenceSteps = 1,
        maxInferenceSteps = 50,
        minGuidanceScale = 0f,
        maxGuidanceScale = 35f,
        supportedAccelerations = setOf(FalAiAcceleration.NONE, FalAiAcceleration.REGULAR),
    ),
    FLUX_LORA_IMAGE_TO_IMAGE(
        alias = "fal-ai/flux-lora/image-to-image",
        displayName = "flux-lora/image-to-image (Image to Image)",
        generationMode = FalAiGenerationMode.IMAGE_TO_IMAGE,
        minInferenceSteps = 1,
        maxInferenceSteps = 50,
        minGuidanceScale = 0f,
        maxGuidanceScale = 35f,
        supportedAccelerations = setOf(FalAiAcceleration.NONE, FalAiAcceleration.REGULAR),
    );

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `default` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val default: FalAiModel = FLUX_DEV

        /**
         * Exposes the `defaultTextToImage` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val defaultTextToImage: FalAiModel = FLUX_DEV

        /**
         * Exposes the `defaultImageToImage` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val defaultImageToImage: FalAiModel = FLUX_DEV_IMAGE_TO_IMAGE

        /**
         * Executes the `parse` step in the SDAI domain layer.
         *
         * @param value value used by the operation.
         * @param fallback fallback value consumed by the API.
         * @return Result produced by `parse`.
         * @author Dmitriy Moroz
         */
        fun parse(value: String?, fallback: FalAiModel = default): FalAiModel =
            entries.firstOrNull { it.alias == value || it.name == value } ?: fallback

        /**
         * Exposes the `textToImage` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val textToImage: List<FalAiModel> =
            entries.filter { it.generationMode == FalAiGenerationMode.TEXT_TO_IMAGE }

        /**
         * Exposes the `imageToImage` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val imageToImage: List<FalAiModel> =
            entries.filter { it.generationMode == FalAiGenerationMode.IMAGE_TO_IMAGE }
    }
}

/**
 * Coordinates supported Fal.ai generation modes in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class FalAiGenerationMode {
    TEXT_TO_IMAGE,
    IMAGE_TO_IMAGE,
}

/**
 * Coordinates supported Fal.ai image size presets in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class FalAiImageSize(
    /**
     * Exposes the `key` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val key: String,
    /**
     * Exposes the `displayName` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val displayName: String,
    /**
     * Exposes the `width` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val height: Int,
) {
    SQUARE_HD("square_hd", "Square HD", 1024, 1024),
    SQUARE("square", "Square", 512, 512),
    PORTRAIT_4_3("portrait_4_3", "Portrait 4:3", 768, 1024),
    PORTRAIT_16_9("portrait_16_9", "Portrait 16:9", 576, 1024),
    LANDSCAPE_4_3("landscape_4_3", "Landscape 4:3", 1024, 768),
    LANDSCAPE_16_9("landscape_16_9", "Landscape 16:9", 1024, 576);

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `default` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val default: FalAiImageSize = LANDSCAPE_4_3

        /**
         * Executes the `parse` step in the SDAI domain layer.
         *
         * @param value value used by the operation.
         * @return Result produced by `parse`.
         * @author Dmitriy Moroz
         */
        fun parse(value: String?): FalAiImageSize =
            entries.firstOrNull { it.key == value || it.name == value } ?: default
    }
}

/**
 * Coordinates supported Fal.ai acceleration presets in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class FalAiAcceleration(
    /**
     * Exposes the `key` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val key: String,
    /**
     * Exposes the `displayName` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val displayName: String,
) {
    NONE("none", "None"),
    REGULAR("regular", "Regular"),
    HIGH("high", "High");

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `default` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val default: FalAiAcceleration = NONE

        /**
         * Executes the `parse` step in the SDAI domain layer.
         *
         * @param value value used by the operation.
         * @return Result produced by `parse`.
         * @author Dmitriy Moroz
         */
        fun parse(value: String?): FalAiAcceleration =
            entries.firstOrNull { it.key == value || it.name == value } ?: default
    }
}
