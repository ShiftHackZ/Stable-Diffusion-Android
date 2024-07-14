package com.shifthackz.aisdv1.domain.mocks

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

val mockStableDiffusionModels = listOf(
    StableDiffusionModel(
        title = "model",
        modelName = "name",
        hash = "hash",
        sha256 = "sha256",
        filename = "filename",
        config = "config",
    ),
    StableDiffusionModel(
        title = "checkpoint",
        modelName = "checkpoint",
        hash = "hash_2",
        sha256 = "sha256_2",
        filename = "filename_2",
        config = "config_2",
    ),
)
