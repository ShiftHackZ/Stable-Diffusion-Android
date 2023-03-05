package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class StableDiffusionTextToImageRepositoryImpl(
    private val remoteDataSource: StableDiffusionTextToImageDataSource.Remote,
    private val localDataSource: GenerationResultDataSource.Local,
) : StableDiffusionTextToImageRepository {

    override fun checkApiAvailability(): Completable = remoteDataSource.checkAvailability()

    override fun generateAndGetImage(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain> =
        remoteDataSource
            .textToImage(payload)
            .flatMap { aiResult ->
                localDataSource
                    .insert(aiResult)
                    .map { id -> aiResult.copy(id = id) }
            }
}
