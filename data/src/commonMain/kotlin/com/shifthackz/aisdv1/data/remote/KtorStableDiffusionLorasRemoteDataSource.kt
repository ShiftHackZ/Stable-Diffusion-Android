package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToLoraDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

class KtorStableDiffusionLorasRemoteDataSource(
    private val api: Automatic1111MetadataApi,
) : LorasDataSource.Remote.Automatic1111 {

    override suspend fun fetchLoras(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): List<LoRA> = api
        .fetchLoras(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToLoraDomain()
}
