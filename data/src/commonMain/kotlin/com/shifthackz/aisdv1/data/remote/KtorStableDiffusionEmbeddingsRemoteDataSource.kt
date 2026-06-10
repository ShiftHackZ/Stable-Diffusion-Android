package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToEmbeddingDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

class KtorStableDiffusionEmbeddingsRemoteDataSource(
    private val api: Automatic1111MetadataApi,
) : EmbeddingsDataSource.Remote.Automatic1111 {

    override suspend fun fetchEmbeddings(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): List<Embedding> = api
        .fetchEmbeddings(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToEmbeddingDomain()
}
