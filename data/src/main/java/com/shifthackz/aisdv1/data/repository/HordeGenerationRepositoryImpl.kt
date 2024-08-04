package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

internal class HordeGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    preferenceManager: PreferenceManager,
    private val remoteDataSource: HordeGenerationDataSource.Remote,
    private val statusSource: HordeGenerationDataSource.StatusSource,
) : CoreGenerationRepository(
    mediaStoreGateway,
    base64ToBitmapConverter,
    localDataSource,
    preferenceManager,
), HordeGenerationRepository {

    override fun observeStatus(): Flowable<HordeProcessStatus> = statusSource.observe()

    override fun validateApiKey(): Single<Boolean> = remoteDataSource.validateApiKey()

    override fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult> = remoteDataSource
        .textToImage(payload)
        .flatMap(::insertGenerationResult)

    override fun generateFromImage(payload: ImageToImagePayload): Single<AiGenerationResult> = remoteDataSource
        .imageToImage(payload)
        .flatMap(::insertGenerationResult)

    override fun interruptGeneration(): Completable = remoteDataSource.interruptGeneration()
}
