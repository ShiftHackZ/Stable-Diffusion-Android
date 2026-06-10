package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

val mockHuggingFaceModelEntity = HuggingFaceModelEntity(
    id = "black-forest-labs/FLUX.1-schnell",
    name = "FLUX.1 Schnell",
    alias = "black-forest-labs/FLUX.1-schnell",
    source = "https://huggingface.co/black-forest-labs/FLUX.1-schnell",
)

val mockHuggingFaceModelEntities = listOf(
    mockHuggingFaceModelEntity,
)
