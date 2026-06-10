package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Supporter

fun interface SupportersRemoteDataSource {

    suspend fun fetch(): List<Supporter>
}
