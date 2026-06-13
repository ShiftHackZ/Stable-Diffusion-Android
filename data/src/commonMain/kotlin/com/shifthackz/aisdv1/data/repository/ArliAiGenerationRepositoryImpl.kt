package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.mappers.withModelName
import com.shifthackz.aisdv1.domain.datasource.ArliAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ArliAiGenerationRepository

/**
 * Implements `ArliAiGenerationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ArliAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val preferenceManager: PreferenceManager,
    private val remoteDataSource: ArliAiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), ArliAiGenerationRepository {

    override suspend fun validateApiKey(): Boolean =
        remoteDataSource.validateApiKey(preferenceManager.arliAiApiKey)

    override suspend fun generateFromText(payload: TextToImagePayload): List<AiGenerationResult> {
        val model = payload.arliAiModel.ifBlank { preferenceManager.arliAiModel }
        return remoteDataSource
            .textToImage(preferenceManager.arliAiApiKey, model, payload)
            .map { result ->
                insertGenerationResult(result.withModelName(model))
            }
    }

    override suspend fun generateFromImage(payload: ImageToImagePayload): List<AiGenerationResult> {
        val model = payload.arliAiModel.ifBlank { preferenceManager.arliAiModel }
        return remoteDataSource
            .imageToImage(preferenceManager.arliAiApiKey, model, payload)
            .map { result ->
                insertGenerationResult(result.withModelName(model))
            }
    }
}
