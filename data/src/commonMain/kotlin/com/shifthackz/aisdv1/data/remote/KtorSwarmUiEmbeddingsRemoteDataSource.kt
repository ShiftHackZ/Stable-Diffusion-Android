package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToEmbeddingDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest

class KtorSwarmUiEmbeddingsRemoteDataSource(
    private val api: SwarmUiModelsApi,
) : EmbeddingsDataSource.Remote.SwarmUi {

    override suspend fun fetchEmbeddings(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<Embedding> = api
        .fetchModels(
            baseUrl = baseUrl,
            request = SwarmUiModelsRequest(
                sessionId = sessionId,
                subType = "Embedding",
                path = "",
                depth = 3,
            ),
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToEmbeddingDomain()
}
