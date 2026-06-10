package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository

internal class HordeGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val remoteDataSource: HordeGenerationDataSource.Remote,
    private val statusSource: HordeGenerationDataSource.StatusSource,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), HordeGenerationRepository {

    override fun observeStatus() = statusSource.observe()

    override suspend fun validateApiKey() = remoteDataSource.validateApiKey(apiKey())

    override suspend fun generateFromText(payload: TextToImagePayload) =
        insertGenerationResult(remoteDataSource.textToImage(apiKey(), payload))

    override suspend fun generateFromImage(payload: ImageToImagePayload) =
        insertGenerationResult(remoteDataSource.imageToImage(apiKey(), payload))

    override suspend fun interruptGeneration() = remoteDataSource.interruptGeneration(apiKey())

    private fun apiKey() = preferenceManager
        .hordeApiKey
        .takeIf(String::isNotEmpty)
        ?: DEFAULT_HORDE_API_KEY

    private companion object {
        const val DEFAULT_HORDE_API_KEY = "0000000000"
    }
}
