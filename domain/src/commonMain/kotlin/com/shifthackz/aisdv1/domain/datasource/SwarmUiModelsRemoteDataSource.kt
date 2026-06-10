package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `SwarmUiModelsRemoteDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SwarmUiModelsRemoteDataSource {

    /**
     * Loads SDAI data through `getNewSession`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `getNewSession`.
     * @author Dmitriy Moroz
     */
    suspend fun getNewSession(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String

    /**
     * Loads SDAI data through `fetchSwarmModels`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchSwarmModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchSwarmModels(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<SwarmUiModel>
}
