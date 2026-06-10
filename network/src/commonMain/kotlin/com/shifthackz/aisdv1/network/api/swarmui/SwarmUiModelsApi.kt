package com.shifthackz.aisdv1.network.api.swarmui

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.request.SwarmUiModelsRequest
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse
import com.shifthackz.aisdv1.network.response.KtorSwarmUiSessionResponse

/**
 * Defines the `SwarmUiModelsApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface SwarmUiModelsApi {

    /**
     * Loads SDAI data through `getNewSession`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `getNewSession`.
     * @author Dmitriy Moroz
     */
    suspend fun getNewSession(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiSessionResponse

    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param request request value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `fetchModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchModels(
        baseUrl: String,
        request: SwarmUiModelsRequest,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiModelsResponse
}
