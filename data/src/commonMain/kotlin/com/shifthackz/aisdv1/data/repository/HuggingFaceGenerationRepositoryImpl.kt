package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository

/**
 * Implements `HuggingFaceGenerationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class HuggingFaceGenerationRepositoryImpl(
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
    private val remoteDataSource: HuggingFaceGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), HuggingFaceGenerationRepository {

    override suspend fun validateApiKey() =
        remoteDataSource.validateApiKey(preferenceManager.huggingFaceApiKey)

    override suspend fun generateFromText(payload: TextToImagePayload) = remoteDataSource
        .textToImage(
            apiKey = preferenceManager.huggingFaceApiKey,
            modelName = preferenceManager.safeHuggingFaceModel,
            payload = payload,
        )
        .let { insertGenerationResult(it) }

    override suspend fun generateFromImage(payload: ImageToImagePayload) = remoteDataSource
        .imageToImage(
            apiKey = preferenceManager.huggingFaceApiKey,
            modelName = preferenceManager.safeHuggingFaceModel,
            payload = payload,
        )
        .let { insertGenerationResult(it) }

    private val PreferenceManager.safeHuggingFaceModel: String
        get() = huggingFaceModel
            .takeIf(HuggingFaceModel.supportedHfInferenceTextToImageAliases::contains)
            ?: HuggingFaceModel.default.alias
}
