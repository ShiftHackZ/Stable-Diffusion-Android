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

/**
 * Implements `EmbeddingsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class EmbeddingsRepositoryImpl(
    /**
     * Exposes the `rdsA1111` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val rdsA1111: EmbeddingsDataSource.Remote.Automatic1111,
    /**
     * Exposes the `rdsSwarm` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val rdsSwarm: EmbeddingsDataSource.Remote.SwarmUi,
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
    private val lds: EmbeddingsDataSource.Local,
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
) : EmbeddingsRepository {

    /**
     * Loads SDAI data through `fetchEmbeddings`.
     *
     * @author Dmitriy Moroz
     */
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

    /**
     * Loads SDAI data through `fetchAndGetEmbeddings`.
     *
     * @return Result produced by `fetchAndGetEmbeddings`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetEmbeddings(): List<Embedding> {
        runCatching { fetchEmbeddings() }
        return getEmbeddings()
    }

    /**
     * Loads SDAI data through `getEmbeddings`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getEmbeddings(): List<Embedding> = lds.getEmbeddings()

    /**
     * Loads SDAI data through `fetchSwarmEmbeddings`.
     *
     * @return Result produced by `fetchSwarmEmbeddings`.
     * @author Dmitriy Moroz
     */
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
