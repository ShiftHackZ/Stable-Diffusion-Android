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

/**
 * Implements `SwarmUiModelsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class SwarmUiModelsRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: SwarmUiModelsRemoteDataSource,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: SwarmUiModelsDataSource.Local,
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
) : SwarmUiModelsRepository {

    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModels() {
        val models = fetchRemoteModels()
        localDataSource.insertModels(models)
    }

    /**
     * Loads SDAI data through `fetchAndGetModels`.
     *
     * @return Result produced by `fetchAndGetModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetModels(): List<SwarmUiModel> {
        runCatching { fetchModels() }
        return getModels()
    }

    /**
     * Loads SDAI data through `getModels`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<SwarmUiModel> = localDataSource.getModels()

    /**
     * Loads SDAI data through `fetchRemoteModels`.
     *
     * @return Result produced by `fetchRemoteModels`.
     * @author Dmitriy Moroz
     */
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
    ): String = remoteDataSource
        .getNewSession(baseUrl, credentials)
        .also { sessionId -> sessionPreference.swarmUiSessionId = sessionId }
}
