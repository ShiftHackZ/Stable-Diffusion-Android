package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest

/**
 * Coordinates `KtorSwarmUiModelsRemoteDataSource` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
class KtorSwarmUiModelsRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val api: SwarmUiModelsApi,
) : SwarmUiModelsRemoteDataSource {

    /**
     * Loads SDAI data through `getNewSession`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `getNewSession`.
     * @author Dmitriy Moroz
     */
    override suspend fun getNewSession(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String = api
        .getNewSession(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .sessionId
        ?.takeIf(String::isNotBlank)
        ?: throw IllegalStateException("Bad session ID.")

    /**
     * Loads SDAI data through `fetchSwarmModels`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchSwarmModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchSwarmModels(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<SwarmUiModel> = api
        .fetchModels(
            baseUrl = baseUrl,
            request = SwarmUiModelsRequest(
                sessionId = sessionId,
                subType = "Stable-Diffusion",
                path = "",
                depth = 3,
            ),
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToCheckpointDomain()
}
