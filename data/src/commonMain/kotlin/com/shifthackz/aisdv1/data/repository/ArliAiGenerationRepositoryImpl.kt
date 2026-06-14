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
 * Generates images through ArliAI and persists returned gallery records.
 *
 * Each generation uses a payload model override when present, otherwise it falls back to the
 * selected ArliAI model saved in preferences.
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

    /**
     * Validates the currently saved ArliAI API key.
     *
     * @return `true` when ArliAI accepts the key.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun validateApiKey(): Boolean =
        remoteDataSource.validateApiKey(preferenceManager.arliAiApiKey)

    /**
     * Generates text-to-image results and saves each returned image to local history.
     *
     * @param payload generation settings and optional ArliAI model override.
     * @return persisted generation records.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromText(payload: TextToImagePayload): List<AiGenerationResult> {
        val model = payload.arliAiModel.ifBlank { preferenceManager.arliAiModel }
        return remoteDataSource
            .textToImage(preferenceManager.arliAiApiKey, model, payload)
            .map { result ->
                insertGenerationResult(result.withModelName(model))
            }
    }

    /**
     * Generates image-to-image results and saves each returned image to local history.
     *
     * @param payload generation settings, source image data, and optional ArliAI model override.
     * @return persisted generation records.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromImage(payload: ImageToImagePayload): List<AiGenerationResult> {
        val model = payload.arliAiModel.ifBlank { preferenceManager.arliAiModel }
        return remoteDataSource
            .imageToImage(preferenceManager.arliAiApiKey, model, payload)
            .map { result ->
                insertGenerationResult(result.withModelName(model))
            }
    }
}
