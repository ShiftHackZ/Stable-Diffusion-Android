package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates `OpenAiSize` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class OpenAiSize(
    /**
     * Exposes the `key` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val key: String,
    /**
     * Exposes the `supportedModels` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val supportedModels: Set<OpenAiModel>,
) {
    W1024_H1024(
        key = "1024x1024",
        supportedModels = setOf(
            OpenAiModel.GPT_IMAGE_2,
            OpenAiModel.GPT_IMAGE_1_5,
            OpenAiModel.GPT_IMAGE_1,
            OpenAiModel.GPT_IMAGE_1_MINI,
        ),
    ),
    W1536_H1024(
        key = "1536x1024",
        supportedModels = setOf(
            OpenAiModel.GPT_IMAGE_2,
            OpenAiModel.GPT_IMAGE_1_5,
            OpenAiModel.GPT_IMAGE_1,
            OpenAiModel.GPT_IMAGE_1_MINI,
        ),
    ),
    W1024_H1536(
        key = "1024x1536",
        supportedModels = setOf(
            OpenAiModel.GPT_IMAGE_2,
            OpenAiModel.GPT_IMAGE_1_5,
            OpenAiModel.GPT_IMAGE_1,
            OpenAiModel.GPT_IMAGE_1_MINI,
        ),
    ),
    W2048_H2048(
        key = "2048x2048",
        supportedModels = setOf(OpenAiModel.GPT_IMAGE_2),
    ),
    W2048_H1152(
        key = "2048x1152",
        supportedModels = setOf(OpenAiModel.GPT_IMAGE_2),
    ),
    W3840_H2160(
        key = "3840x2160",
        supportedModels = setOf(OpenAiModel.GPT_IMAGE_2),
    ),
    W2160_H3840(
        key = "2160x3840",
        supportedModels = setOf(OpenAiModel.GPT_IMAGE_2),
    );

    /**
     * Exposes the `width` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val width: Int
        get() = key.split("x").first().toInt()

    /**
     * Exposes the `height` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val height: Int
        get() = key.split("x").last().toInt()
}
