package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException

internal class SwarmUiModelsRepositoryImpl(
    private val remoteDataSource: SwarmUiModelsRemoteDataSource,
    private val localDataSource: SwarmUiModelsDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val sessionPreference: SessionPreference,
    private val authorizationStore: AuthorizationStore,
) : SwarmUiModelsRepository {

    override suspend fun fetchModels() {
        val models = fetchRemoteModels()
        localDataSource.insertModels(models)
    }

    override suspend fun fetchAndGetModels(): List<SwarmUiModel> {
        runCatching { fetchModels() }
        return getModels()
    }

    override suspend fun getModels(): List<SwarmUiModel> = localDataSource.getModels()

    private suspend fun fetchRemoteModels(): List<SwarmUiModel> {
        val baseUrl = preferenceManager.swarmUiServerUrl
        val credentials = authorizationStore.getAuthorizationCredentials()
        val sessionId = getSessionId(baseUrl, credentials)
        return try {
            remoteDataSource.fetchSwarmModels(baseUrl, sessionId, credentials)
        } catch (e: SwarmUiBadSessionException) {
            val renewedSessionId = forceRenewSession(baseUrl, credentials)
            remoteDataSource.fetchSwarmModels(baseUrl, renewedSessionId, credentials)
        }
    }

    private suspend fun getSessionId(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String = sessionPreference
        .swarmUiSessionId
        .takeIf(String::isNotBlank)
        ?: forceRenewSession(baseUrl, credentials)

    private suspend fun forceRenewSession(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String = remoteDataSource
        .getNewSession(baseUrl, credentials)
        .also { sessionId -> sessionPreference.swarmUiSessionId = sessionId }
}
