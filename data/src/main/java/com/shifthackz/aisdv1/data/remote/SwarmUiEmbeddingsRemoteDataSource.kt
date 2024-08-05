package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToEmbeddingDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_MODELS
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import io.reactivex.rxjava3.core.Single

class SwarmUiEmbeddingsRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: SwarmUiApi,
) : EmbeddingsDataSource.Remote.SwarmUi {

    override fun fetchEmbeddings(sessionId: String): Single<List<Embedding>> = serverUrlProvider(PATH_MODELS)
        .flatMap { url ->
            val request = SwarmUiModelsRequest(
                sessionId = sessionId,
                subType = "Embedding",
                path = "",
                depth = 3,
            )
            api.fetchModels(url, request)
        }
        .map(SwarmUiModelsResponse::mapRawToEmbeddingDomain)
}
