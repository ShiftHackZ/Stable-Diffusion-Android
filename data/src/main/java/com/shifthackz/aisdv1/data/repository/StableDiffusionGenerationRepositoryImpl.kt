package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

class StableDiffusionGenerationRepositoryImpl(
    private val remoteDataSource: StableDiffusionTextToImageDataSource.Remote,
    private val localDataSource: GenerationResultDataSource.Local,
) : StableDiffusionGenerationRepository {

    override fun checkApiAvailability() = remoteDataSource.checkAvailability()

    override fun generateFromText(payload: TextToImagePayload) = remoteDataSource
        .textToImage(payload)
        .flatMap(::insertGenerationResult)

    override fun generateFromImage(payload: ImageToImagePayload) = remoteDataSource
        .imageToImage(payload)
        .flatMap(::insertGenerationResult)

    private fun insertGenerationResult(ai: AiGenerationResult) = localDataSource
        .insert(ai)
        .map { id -> ai.copy(id) }
}
