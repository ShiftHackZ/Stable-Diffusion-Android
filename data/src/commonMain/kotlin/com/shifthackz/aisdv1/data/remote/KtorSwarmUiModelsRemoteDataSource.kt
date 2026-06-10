package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToCheckpointDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest

class KtorSwarmUiModelsRemoteDataSource(
    private val api: SwarmUiModelsApi,
) : SwarmUiModelsRemoteDataSource {

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
