package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity
import java.util.Date

val mockSupporterEntities = listOf(
    SupporterEntity(
        id = 5598,
        name = "NZ",
        date = Date(1998, 5, 5),
        message = "I always wanted support you ❤",
    ),
)
