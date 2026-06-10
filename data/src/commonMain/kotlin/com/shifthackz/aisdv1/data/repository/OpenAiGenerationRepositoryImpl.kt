package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository

internal class OpenAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val remoteDataSource: OpenAiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), OpenAiGenerationRepository {

    override suspend fun validateApiKey() =
        remoteDataSource.validateApiKey(preferenceManager.openAiApiKey)

    override suspend fun generateFromText(payload: TextToImagePayload) = remoteDataSource
        .textToImage(preferenceManager.openAiApiKey, payload)
        .let { ai -> insertGenerationResult(ai) }
}
