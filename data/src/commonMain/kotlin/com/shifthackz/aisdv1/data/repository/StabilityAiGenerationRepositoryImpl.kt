package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository

internal class StabilityAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    backgroundWorkObserver: BackgroundWorkObserver,
    localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val generationRds: StabilityAiGenerationDataSource.Remote,
    private val creditsRds: StabilityAiCreditsRemoteDataSource,
    private val creditsLds: StabilityAiCreditsDataSource.Local,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), StabilityAiGenerationRepository {

    override suspend fun validateApiKey() =
        generationRds.validateApiKey(preferenceManager.stabilityAiApiKey)

    override suspend fun generateFromText(payload: TextToImagePayload) = generationRds
        .textToImage(
            apiKey = preferenceManager.stabilityAiApiKey,
            engineId = preferenceManager.stabilityAiEngineId,
            payload = payload,
        )
        .let { insertGenerationResult(it) }
        .let { refreshCredits(it) }

    override suspend fun generateFromImage(payload: ImageToImagePayload) = generationRds
        .imageToImage(
            apiKey = preferenceManager.stabilityAiApiKey,
            engineId = preferenceManager.stabilityAiEngineId,
            payload = payload,
        )
        .let { insertGenerationResult(it) }
        .let { refreshCredits(it) }

    private suspend fun refreshCredits(ai: AiGenerationResult): AiGenerationResult {
        runCatching {
            creditsRds
                .fetch(preferenceManager.stabilityAiApiKey)
                .let { credits -> creditsLds.save(credits) }
        }
        return ai
    }
}
