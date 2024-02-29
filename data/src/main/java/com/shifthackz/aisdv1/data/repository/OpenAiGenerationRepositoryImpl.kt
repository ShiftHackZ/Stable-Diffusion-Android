package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class OpenAiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    preferenceManager: PreferenceManager,
    private val remoteDataSource: OpenAiGenerationDataSource.Remote,
) : CoreGenerationRepository(
    mediaStoreGateway,
    base64ToBitmapConverter,
    localDataSource,
    preferenceManager,
), OpenAiGenerationRepository {

    override fun validateApiKey() = remoteDataSource.validateApiKey()

    override fun generateFromText(payload: TextToImagePayload) = remoteDataSource
        .textToImage(payload)
        .flatMap(::insertGenerationResult)
}
