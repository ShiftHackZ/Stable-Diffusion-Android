package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException

internal class EmbeddingsRepositoryImpl(
    private val rdsA1111: EmbeddingsDataSource.Remote.Automatic1111,
    private val rdsSwarm: EmbeddingsDataSource.Remote.SwarmUi,
    private val swarmSessionRemoteDataSource: SwarmUiModelsRemoteDataSource,
    private val lds: EmbeddingsDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val sessionPreference: SessionPreference,
    private val authorizationStore: AuthorizationStore,
) : EmbeddingsRepository {

    override suspend fun fetchEmbeddings() {
        val embeddings = when (preferenceManager.source) {
            ServerSource.AUTOMATIC1111 -> rdsA1111.fetchEmbeddings(
                baseUrl = preferenceManager.automatic1111ServerUrl,
                credentials = authorizationStore.getAuthorizationCredentials(),
            )

            ServerSource.SWARM_UI -> fetchSwarmEmbeddings()

            else -> return
        }
        lds.insertEmbeddings(embeddings)
    }

    override suspend fun fetchAndGetEmbeddings(): List<Embedding> {
        runCatching { fetchEmbeddings() }
        return getEmbeddings()
    }

    override suspend fun getEmbeddings(): List<Embedding> = lds.getEmbeddings()

    private suspend fun fetchSwarmEmbeddings(): List<Embedding> {
        val baseUrl = preferenceManager.swarmUiServerUrl
        val credentials = authorizationStore.getAuthorizationCredentials()
        val sessionId = getSessionId(baseUrl, credentials)
        return try {
            rdsSwarm.fetchEmbeddings(baseUrl, sessionId, credentials)
        } catch (e: SwarmUiBadSessionException) {
            val renewedSessionId = forceRenewSession(baseUrl, credentials)
            rdsSwarm.fetchEmbeddings(baseUrl, renewedSessionId, credentials)
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
    ): String = swarmSessionRemoteDataSource
        .getNewSession(baseUrl, credentials)
        .also { sessionId -> sessionPreference.swarmUiSessionId = sessionId }
}
