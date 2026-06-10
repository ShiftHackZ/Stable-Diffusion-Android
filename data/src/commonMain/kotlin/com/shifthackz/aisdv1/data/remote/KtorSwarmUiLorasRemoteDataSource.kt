package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToLoraDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest

class KtorSwarmUiLorasRemoteDataSource(
    private val api: SwarmUiModelsApi,
) : LorasDataSource.Remote.SwarmUi {

    override suspend fun fetchLoras(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<LoRA> = api
        .fetchModels(
            baseUrl = baseUrl,
            request = SwarmUiModelsRequest(
                sessionId = sessionId,
                subType = "LoRA",
                path = "",
                depth = 3,
            ),
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToLoraDomain()
}
