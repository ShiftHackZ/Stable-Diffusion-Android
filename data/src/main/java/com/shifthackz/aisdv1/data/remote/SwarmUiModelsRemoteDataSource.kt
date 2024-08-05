package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_MODELS
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import io.reactivex.rxjava3.core.Single

internal class SwarmUiModelsRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: SwarmUiApi,
) : SwarmUiModelsDataSource.Remote {

    override fun fetchSwarmModels(sessionId: String): Single<List<SwarmUiModel>> = PATH_MODELS
        .let(serverUrlProvider::invoke)
        .flatMap { url ->
            val request = SwarmUiModelsRequest(
                sessionId = sessionId,
                subType = "Stable-Diffusion",
                path = "",
                depth = 3,
            )
            api.fetchModels(url, request)
        }
        .map(SwarmUiModelsResponse::mapRawToCheckpointDomain)
}
