package com.shifthackz.aisdv1.network.api.swarmui

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.response.KtorSwarmUiGenerationResponse

/**
 * Defines the `SwarmUiGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface SwarmUiGenerationApi {

    /**
     * Executes the `generate` step in the SDAI network layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param request request value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `generate`.
     * @author Dmitriy Moroz
     */
    suspend fun generate(
        baseUrl: String,
        request: SwarmUiGenerationRequest,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiGenerationResponse

    /**
     * Executes the `downloadImage` step in the SDAI network layer.
     *
     * @param url remote URL used by the operation.
     * @param authorization authorization value consumed by the API.
     * @return Result produced by `downloadImage`.
     * @author Dmitriy Moroz
     */
    suspend fun downloadImage(
        url: String,
        authorization: BasicHttpAuthorization?,
    ): ByteArray
}
