package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.domain.datasource.SupportersRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi

class KtorSupportersRemoteDataSource(
    private val api: SdaiAppApi,
) : SupportersRemoteDataSource {

    override suspend fun fetch(): List<Supporter> = api
        .fetchSupporters()
        .mapRawToDomain()
}
