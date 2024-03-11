package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class StabilityAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val generationRds: StabilityAiGenerationDataSource.Remote,
    private val creditsRds: StabilityAiCreditsDataSource.Remote,
    private val creditsLds: StabilityAiCreditsDataSource.Local,
) : CoreGenerationRepository(
    mediaStoreGateway,
    base64ToBitmapConverter,
    localDataSource,
    preferenceManager,
), StabilityAiGenerationRepository {

    override fun validateApiKey() = generationRds.validateApiKey()

    override fun generateFromText(payload: TextToImagePayload) = generationRds
        .textToImage(preferenceManager.stabilityAiEngineId, payload)
        .flatMap(::insertGenerationResult)
        .flatMap(::refreshCredits)

    override fun generateFromImage(payload: ImageToImagePayload) = generationRds
        .imageToImage(preferenceManager.stabilityAiEngineId, payload)
        .flatMap(::insertGenerationResult)
        .flatMap(::refreshCredits)

    private fun refreshCredits(ai: AiGenerationResult) = creditsRds
        .fetch()
        .flatMapCompletable(creditsLds::save)
        .andThen(Single.just(ai))
}
