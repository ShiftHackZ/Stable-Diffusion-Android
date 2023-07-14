package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository

internal class HordeGenerationRepositoryImpl(
    localDataSource: GenerationResultDataSource.Local,
    preferenceManager: PreferenceManager,
    private val remoteDataSource: HordeGenerationDataSource.Remote,
    private val statusSource: HordeGenerationDataSource.StatusSource,
) : CoreGenerationRepository(localDataSource, preferenceManager), HordeGenerationRepository {

    override fun observeStatus() = statusSource.observe()

    override fun generateFromText(payload: TextToImagePayload) = remoteDataSource
        .textToImage(payload)
        .flatMap(::insertGenerationResult)


    override fun generateFromImage(payload: ImageToImagePayload) = remoteDataSource
        .imageToImage(payload)
        .flatMap(::insertGenerationResult)
}
