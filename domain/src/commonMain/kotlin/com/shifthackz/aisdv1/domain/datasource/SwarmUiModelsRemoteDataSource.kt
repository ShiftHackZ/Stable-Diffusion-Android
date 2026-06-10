package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

interface SwarmUiModelsRemoteDataSource {

    suspend fun getNewSession(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String

    suspend fun fetchSwarmModels(
        baseUrl: String,
        sessionId: String,
        credentials: AuthorizationCredentials,
    ): List<SwarmUiModel>
}
