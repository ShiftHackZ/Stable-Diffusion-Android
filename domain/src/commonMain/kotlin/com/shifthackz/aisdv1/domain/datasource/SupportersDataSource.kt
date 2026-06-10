package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Supporter

sealed interface SupportersDataSource {

    interface Local : SupportersDataSource {
        suspend fun save(data: List<Supporter>)
        suspend fun getAll(): List<Supporter>
    }
}
