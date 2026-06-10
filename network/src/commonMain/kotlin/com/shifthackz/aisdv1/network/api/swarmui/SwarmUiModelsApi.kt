package com.shifthackz.aisdv1.network.api.swarmui

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse
import com.shifthackz.aisdv1.network.response.KtorSwarmUiSessionResponse

interface SwarmUiModelsApi {

    suspend fun getNewSession(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiSessionResponse

    suspend fun fetchModels(
        baseUrl: String,
        request: SwarmUiModelsRequest,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiModelsResponse
}
