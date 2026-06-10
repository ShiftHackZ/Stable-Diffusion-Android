package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import com.shifthackz.aisdv1.network.exception.SwarmUiBadSessionException

internal class SwarmUiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val preferenceManager: PreferenceManager,
    private val sessionPreference: SessionPreference,
    private val authorizationStore: AuthorizationStore,
    private val swarmSessionRemoteDataSource: SwarmUiModelsRemoteDataSource,
    private val remoteDataSource: SwarmUiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), SwarmUiGenerationRepository {

    override suspend fun checkApiAvailability() {
        getSessionId(
            baseUrl = preferenceManager.swarmUiServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
    }

    override suspend fun checkApiAvailability(url: String) {
        forceRenewSession(
            baseUrl = url,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
    }

    override suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult {
        val baseUrl = preferenceManager.swarmUiServerUrl
        val credentials = authorizationStore.getAuthorizationCredentials()
        val model = preferenceManager.swarmUiModel
        val sessionId = getSessionId(baseUrl, credentials)
        val ai = try {
            remoteDataSource.textToImage(baseUrl, sessionId, model, credentials, payload)
        } catch (e: SwarmUiBadSessionException) {
            val renewedSessionId = forceRenewSession(baseUrl, credentials)
            remoteDataSource.textToImage(baseUrl, renewedSessionId, model, credentials, payload)
        }
        return insertGenerationResult(ai)
    }

    override suspend fun generateFromImage(payload: ImageToImagePayload): AiGenerationResult {
        val baseUrl = preferenceManager.swarmUiServerUrl
        val credentials = authorizationStore.getAuthorizationCredentials()
        val model = preferenceManager.swarmUiModel
        val sessionId = getSessionId(baseUrl, credentials)
        val ai = try {
            remoteDataSource.imageToImage(baseUrl, sessionId, model, credentials, payload)
        } catch (e: SwarmUiBadSessionException) {
            val renewedSessionId = forceRenewSession(baseUrl, credentials)
            remoteDataSource.imageToImage(baseUrl, renewedSessionId, model, credentials, payload)
        }
        return insertGenerationResult(ai)
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
