package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionEmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_EMBEDDINGS
import com.shifthackz.aisdv1.network.response.SdEmbeddingsResponse
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionEmbeddingsRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : StableDiffusionEmbeddingsDataSource.Remote {

    override fun fetchEmbeddings(): Single<List<StableDiffusionEmbedding>> = serverUrlProvider(PATH_EMBEDDINGS)
        .flatMap(api::fetchEmbeddings)
        .map(SdEmbeddingsResponse::mapRawToDomain)
}
