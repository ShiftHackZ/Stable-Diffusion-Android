package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class SwarmUiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    private val remoteDataSource: SwarmUiGenerationDataSource.Remote,
    private val preferenceManager: PreferenceManager,
    private val sessionPreference: SessionPreference,
) : CoreGenerationRepository(
    mediaStoreGateway,
    base64ToBitmapConverter,
    localDataSource,
    preferenceManager,
), SwarmUiGenerationRepository {

    override fun checkApiAvailability() = obtainSessionId()
        .ignoreElement()

    override fun checkApiAvailability(url: String) = obtainSessionId(url)
        .ignoreElement()

    override fun generateFromText(payload: TextToImagePayload) = obtainSessionId()
        .flatMap { sessionId -> remoteDataSource.textToImage(sessionId, payload) }
        .flatMap(::insertGenerationResult)

    private fun obtainSessionId(connectUrl: String? = null) =
        if (sessionPreference.swarmUiSessionId.isBlank()) {
            val chain = connectUrl
                ?.let(remoteDataSource::getNewSession)
                ?: remoteDataSource.getNewSession()

            chain.map { sessionId ->
                sessionPreference.swarmUiSessionId = sessionId
                sessionId
            }
        } else {
            Single.just(sessionPreference.swarmUiSessionId)
        }
}
