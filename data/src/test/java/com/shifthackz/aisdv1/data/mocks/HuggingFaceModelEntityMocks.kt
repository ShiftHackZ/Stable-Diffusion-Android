package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

val mockHuggingFaceModelEntity = HuggingFaceModelEntity(
    id = "050598",
    name = "Super model",
    alias = "❤",
    source = "https://life.archive.org/models/unique/050598",
)

val mockHuggingFaceModelEntities = listOf(
    mockHuggingFaceModelEntity,
)
