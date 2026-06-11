package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.mappers.toModelName
import com.shifthackz.aisdv1.data.mappers.withModelName
import com.shifthackz.aisdv1.domain.datasource.FalAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.FalAiGenerationRepository

/**
 * Implements `FalAiGenerationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class FalAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: FalAiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), FalAiGenerationRepository {

    override suspend fun validateApiKey(): Boolean =
        remoteDataSource.validateApiKey(preferenceManager.falAiApiKey)

    override suspend fun generateFromText(payload: TextToImagePayload) = remoteDataSource
        .textToImage(preferenceManager.falAiApiKey, payload)
        .map { result ->
            insertGenerationResult(result.withModelName(payload.falAiModel.toModelName()))
        }

    override suspend fun generateFromImage(payload: ImageToImagePayload) = remoteDataSource
        .imageToImage(preferenceManager.falAiApiKey, payload)
        .map { result ->
            insertGenerationResult(result.withModelName(payload.falAiModel.toModelName()))
        }
}
