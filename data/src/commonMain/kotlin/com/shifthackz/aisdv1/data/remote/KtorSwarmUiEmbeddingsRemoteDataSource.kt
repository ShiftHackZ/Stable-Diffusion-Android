package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToEmbeddingDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest

/**
 * Coordinates `KtorSwarmUiEmbeddingsRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorSwarmUiEmbeddingsRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: SwarmUiModelsApi,
) : EmbeddingsDataSource.Remote.SwarmUi {

    /**
     * Loads SDAI data through `fetchEmbeddings`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchEmbeddings`.
     * @author Dmitriy Moroz
     */
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
