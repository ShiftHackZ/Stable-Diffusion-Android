package com.shifthackz.aisdv1.domain.entity

enum class OpenAiSize(
    val key: String,
    val supportedModels: Set<OpenAiModel>,
) {
    W256_H256(
        key = "256x256",
        supportedModels = setOf(OpenAiModel.DALL_E_2),
    ),
    W512_H512(
        key = "512x512",
        supportedModels = setOf(OpenAiModel.DALL_E_2),
    ),
    W1024_H1024(
        key = "1024x1024",
        supportedModels = setOf(OpenAiModel.DALL_E_2, OpenAiModel.DALL_E_3),
    ),
    W1792_H1024(
        key = "1792x1024",
        supportedModels = setOf(OpenAiModel.DALL_E_3),
    ),
    W1024_H1792(
        key = "1024x1792",
        supportedModels = setOf(OpenAiModel.DALL_E_3),
    );

    val width: Int
        get() = key.split("x").first().toInt()

    val height: Int
        get() = key.split("x").last().toInt()
}
