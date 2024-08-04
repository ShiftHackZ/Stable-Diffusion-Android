package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class HuggingFaceGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val remoteDataSource: HuggingFaceGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway,
    base64ToBitmapConverter,
    localDataSource,
    preferenceManager,
), HuggingFaceGenerationRepository {

    override fun validateApiKey(): Single<Boolean> = remoteDataSource.validateApiKey()

    override fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult> = remoteDataSource
        .textToImage(preferenceManager.huggingFaceModel, payload)
        .flatMap(::insertGenerationResult)

    override fun generateFromImage(payload: ImageToImagePayload): Single<AiGenerationResult> = remoteDataSource
        .imageToImage(preferenceManager.huggingFaceModel, payload)
        .flatMap(::insertGenerationResult)
}
