package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

val mockHuggingFaceModel = HuggingFaceModel(
    id = "050598",
    name = "Super model",
    alias = "❤",
    source = "https://life.archive.org/models/unique/050598",
)

val mockHuggingFaceModels = listOf(mockHuggingFaceModel)
