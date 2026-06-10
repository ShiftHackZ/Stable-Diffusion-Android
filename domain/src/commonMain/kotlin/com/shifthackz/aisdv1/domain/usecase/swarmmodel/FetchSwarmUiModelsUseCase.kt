package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `FetchSwarmUiModelsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface FetchSwarmUiModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials = AuthorizationCredentials.None,
    ): List<SwarmUiModel>
}

/**
 * Implements `FetchSwarmUiModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class FetchSwarmUiModelsUseCaseImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: SwarmUiModelsRemoteDataSource,
) : FetchSwarmUiModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param sessionId session id value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<SwarmUiModel> =
        remoteDataSource.fetchSwarmModels(baseUrl, sessionId, credentials)
}
