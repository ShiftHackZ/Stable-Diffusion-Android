package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

val mockLocalAiModel = LocalAiModel(
    id = "5598",
    type = LocalAiModel.Type.ONNX,
    name = "Model 5598",
    size = "5 Gb",
    sources = listOf("https://example.com/1.html"),
    downloaded = false,
    selected = false,
)

val mockLocalAiModels = listOf(mockLocalAiModel)
