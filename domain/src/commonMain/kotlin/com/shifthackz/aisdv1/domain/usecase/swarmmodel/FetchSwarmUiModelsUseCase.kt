package com.shifthackz.aisdv1.domain.usecase.swarmmodel

import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

interface FetchSwarmUiModelsUseCase {

    suspend operator fun invoke(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials = AuthorizationCredentials.None,
    ): List<SwarmUiModel>
}

class FetchSwarmUiModelsUseCaseImpl(
    private val remoteDataSource: SwarmUiModelsRemoteDataSource,
) : FetchSwarmUiModelsUseCase {

    override suspend fun invoke(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<SwarmUiModel> =
        remoteDataSource.fetchSwarmModels(baseUrl, sessionId, credentials)
}
