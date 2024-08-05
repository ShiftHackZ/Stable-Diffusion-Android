package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToLoraDomain
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi.Companion.PATH_MODELS
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.SwarmUiModelsResponse
import io.reactivex.rxjava3.core.Single

internal class SwarmUiLorasRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: SwarmUiApi,
) : LorasDataSource.Remote.SwarmUi {

    override fun fetchLoras(sessionId: String): Single<List<LoRA>> = serverUrlProvider(PATH_MODELS)
        .flatMap { url ->
            val request = SwarmUiModelsRequest(
                sessionId = sessionId,
                subType = "LoRA",
                path = "",
                depth = 3,
            )
            api.fetchModels(url, request)
        }
        .map(SwarmUiModelsResponse::mapRawToLoraDomain)
}
