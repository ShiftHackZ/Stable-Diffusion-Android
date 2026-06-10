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

/**
 * Implements `LorasRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class LorasRepositoryImpl(
    /**
     * Exposes the `rdsA1111` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val rdsA1111: LorasDataSource.Remote.Automatic1111,
    /**
     * Exposes the `rdsSwarm` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val rdsSwarm: LorasDataSource.Remote.SwarmUi,
    /**
     * Exposes the `swarmSessionRemoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val swarmSessionRemoteDataSource: SwarmUiModelsRemoteDataSource,
    /**
     * Exposes the `lds` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val lds: LorasDataSource.Local,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `sessionPreference` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val sessionPreference: SessionPreference,
    /**
     * Exposes the `authorizationStore` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
) : LorasRepository {

    /**
     * Loads SDAI data through `fetchLoras`.
     *
     * @author Dmitriy Moroz
     */
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

    /**
     * Loads SDAI data through `fetchAndGetLoras`.
     *
     * @return Result produced by `fetchAndGetLoras`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetLoras(): List<LoRA> {
        runCatching { fetchLoras() }
        return getLoras()
    }

    /**
     * Loads SDAI data through `getLoras`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getLoras(): List<LoRA> = lds.getLoras()

    /**
     * Loads SDAI data through `fetchSwarmLoras`.
     *
     * @return Result produced by `fetchSwarmLoras`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Loads SDAI data through `getSessionId`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `getSessionId`.
     * @author Dmitriy Moroz
     */
    private suspend fun getSessionId(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String = sessionPreference
        .swarmUiSessionId
        .takeIf(String::isNotBlank)
        ?: forceRenewSession(baseUrl, credentials)

    /**
     * Executes the `forceRenewSession` step in the SDAI data layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `forceRenewSession`.
     * @author Dmitriy Moroz
     */
    private suspend fun forceRenewSession(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): String = swarmSessionRemoteDataSource
        .getNewSession(baseUrl, credentials)
        .also { sessionId -> sessionPreference.swarmUiSessionId = sessionId }
}
