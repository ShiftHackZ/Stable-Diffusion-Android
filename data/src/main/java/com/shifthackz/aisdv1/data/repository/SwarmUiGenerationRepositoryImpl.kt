package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class SwarmUiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val preferenceManager: PreferenceManager,
    private val session: SwarmUiSessionDataSource,
    private val remoteDataSource: SwarmUiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    base64ToBitmapConverter = base64ToBitmapConverter,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), SwarmUiGenerationRepository {

    override fun checkApiAvailability(): Completable = session
        .getSessionId()
        .ignoreElement()

    override fun checkApiAvailability(url: String): Completable = session
        .getSessionId(url)
        .ignoreElement()

    override fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult> = session
        .getSessionId()
        .flatMap { sessionId ->
            remoteDataSource.textToImage(
                sessionId = sessionId,
                model = preferenceManager.swarmUiModel,
                payload = payload,
            )
        }
        .let(session::handleSessionError)
        .flatMap(::insertGenerationResult)

    override fun generateFromImage(payload: ImageToImagePayload): Single<AiGenerationResult> = session
        .getSessionId()
        .flatMap { sessionId ->
            remoteDataSource.imageToImage(
                sessionId = sessionId,
                model = preferenceManager.swarmUiModel,
                payload = payload,
            )
        }
        .let(session::handleSessionError)
        .flatMap(::insertGenerationResult)
}
