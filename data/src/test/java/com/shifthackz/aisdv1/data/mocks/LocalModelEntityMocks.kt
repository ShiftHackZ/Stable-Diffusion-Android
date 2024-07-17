package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.storage.db.persistent.entity.LocalModelEntity

val mockLocalModelEntity = LocalModelEntity(
    id = "5598",
    name = "Best model in entire universe",
    size = "5598 Gb",
    sources = listOf("https://5598.is.my.favourite.com"),
)

val mockLocalModelEntities = listOf(
    LocalModelEntity(
        id = "1",
        name = "Model 1",
        size = "1 Gb",
        sources = listOf("https://example.com/1.php"),
    ),
    mockLocalModelEntity,
)
