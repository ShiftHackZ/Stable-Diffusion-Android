package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository

internal class StabilityAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val remoteDateSource: StabilityAiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway,
    base64ToBitmapConverter,
    localDataSource,
    preferenceManager,
), StabilityAiGenerationRepository {

    override fun validateApiKey() = remoteDateSource.validateApiKey()

    override fun generateFromText(payload: TextToImagePayload) = remoteDateSource
        .textToImage(preferenceManager.stabilityAiEngineId, payload)
        .flatMap(::insertGenerationResult)

    override fun generateFromImage(payload: ImageToImagePayload) = remoteDateSource
        .imageToImage(preferenceManager.stabilityAiEngineId, payload)
        .flatMap(::insertGenerationResult)
}
