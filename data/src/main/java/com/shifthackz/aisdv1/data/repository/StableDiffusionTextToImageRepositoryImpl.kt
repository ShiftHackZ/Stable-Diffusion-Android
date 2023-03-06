package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class StableDiffusionTextToImageRepositoryImpl(
    private val remoteDataSource: StableDiffusionTextToImageDataSource.Remote,
    private val localDataSource: GenerationResultDataSource.Local,
) : StableDiffusionTextToImageRepository {

    override fun checkApiAvailability(): Completable = remoteDataSource.checkAvailability()

    override fun generateAndGetImage(payload: TextToImagePayload): Single<AiGenerationResult> =
        remoteDataSource
            .textToImage(payload)
            .flatMap { aiResult ->
                localDataSource
                    .insert(aiResult)
                    .map { id -> aiResult.copy(id = id) }
            }
}
