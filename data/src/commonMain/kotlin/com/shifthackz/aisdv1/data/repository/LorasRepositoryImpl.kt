package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException

internal class LorasRepositoryImpl(
    private val rdsA1111: LorasDataSource.Remote.Automatic1111,
    private val rdsSwarm: LorasDataSource.Remote.SwarmUi,
    private val swarmSessionRemoteDataSource: SwarmUiModelsRemoteDataSource,
    private val lds: LorasDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val sessionPreference: SessionPreference,
    private val authorizationStore: AuthorizationStore,
) : LorasRepository {

    override suspend fun fetchLoras() {
        val loras = when (preferenceManager.source) {
            ServerSource.AUTOMATIC1111 -> rdsA1111.fetchLoras(
                baseUrl = preferenceManager.automatic1111ServerUrl,
                credentials = authorizationStore.getAuthorizationCredentials(),
            )

            ServerSource.SWARM_UI -> fetchSwarmLoras()

            else -> return
        }
        lds.insertLoras(loras)
    }

    override suspend fun fetchAndGetLoras(): List<LoRA> {
        runCatching { fetchLoras() }
        return getLoras()
    }

    override suspend fun getLoras(): List<LoRA> = lds.getLoras()

    private suspend fun fetchSwarmLoras(): List<LoRA> {
        val baseUrl = preferenceManager.swarmUiServerUrl
        val credentials = authorizationStore.getAuthorizationCredentials()
        val sessionId = getSessionId(baseUrl, credentials)
        return try {
            rdsSwarm.fetchLoras(baseUrl, sessionId, credentials)
        } catch (e: SwarmUiBadSessionException) {
            val renewedSessionId = forceRenewSession(baseUrl, credentials)
            rdsSwarm.fetchLoras(baseUrl, renewedSessionId, credentials)
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
