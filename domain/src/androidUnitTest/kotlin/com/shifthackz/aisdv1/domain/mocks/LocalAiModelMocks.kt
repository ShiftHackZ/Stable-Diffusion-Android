package com.shifthackz.aisdv1.domain.mocks

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

val mockLocalAiModels = listOf(
    LocalAiModel.CustomOnnx,
    LocalAiModel(
        id = "1",
        type = LocalAiModel.Type.ONNX,
        name = "Model 1",
        size = "5 Gb",
        sources = listOf("https://example.com/1.html"),
        downloaded = false,
        selected = false,
    ),
)

val mockLocalBonsaiModels = listOf(
    LocalAiModel(
        id = "bonsai-1",
        type = LocalAiModel.Type.Bonsai,
        name = "Bonsai Image 4B Ternary MLX 2-bit",
        size = "3.89 GB",
        sources = listOf("https://example.com/bonsai-1.zip"),
        downloaded = true,
        selected = false,
    ),
    LocalAiModel(
        id = "bonsai-2",
        type = LocalAiModel.Type.Bonsai,
        name = "Bonsai Image 4B Binary MLX 1-bit",
        size = "3.42 GB",
        sources = listOf("https://example.com/bonsai-2.zip"),
        downloaded = true,
        selected = false,
    ),
)
