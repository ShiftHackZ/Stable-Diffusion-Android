package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

val mockLocalAiModels = listOf(
    LocalAiModel(
        id = "1",
        name = "Model 1",
        size = "5 Gb",
        sources = listOf("https://example.com/1.html"),
        downloaded = false,
        selected = false,
    ),
)
