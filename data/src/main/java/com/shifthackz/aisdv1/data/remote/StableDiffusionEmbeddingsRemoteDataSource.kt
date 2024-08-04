package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_EMBEDDINGS
import com.shifthackz.aisdv1.network.response.SdEmbeddingsResponse

internal class StableDiffusionEmbeddingsRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : EmbeddingsDataSource.Remote.Automatic1111 {

    override fun fetchEmbeddings() = serverUrlProvider(PATH_EMBEDDINGS)
        .flatMap(api::fetchEmbeddings)
        .map(SdEmbeddingsResponse::mapRawToCheckpointDomain)
}
