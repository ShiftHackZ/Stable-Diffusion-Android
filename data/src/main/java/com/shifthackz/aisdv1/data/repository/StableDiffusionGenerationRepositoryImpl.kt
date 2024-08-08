package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    backgroundWorkObserver: BackgroundWorkObserver,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    private val remoteDataSource: StableDiffusionGenerationDataSource.Remote,
    private val preferenceManager: PreferenceManager,
    private val textToImageDemo: TextToImageDemo,
    private val imageToImageDemo: ImageToImageDemo,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    base64ToBitmapConverter = base64ToBitmapConverter,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), StableDiffusionGenerationRepository {

    override fun checkApiAvailability() = remoteDataSource.checkAvailability()

    override fun checkApiAvailability(url: String) = remoteDataSource.checkAvailability(url)

    override fun generateFromText(payload: TextToImagePayload): Single<AiGenerationResult> {
        val chain =
            if (preferenceManager.demoMode) textToImageDemo.getDemoBase64(payload)
            else remoteDataSource.textToImage(payload)

        return chain.flatMap(::insertGenerationResult)
    }

    override fun generateFromImage(payload: ImageToImagePayload): Single<AiGenerationResult> {
        val chain =
            if (preferenceManager.demoMode) imageToImageDemo.getDemoBase64(payload)
            else remoteDataSource.imageToImage(payload)

        return chain.flatMap(::insertGenerationResult)
    }

    override fun interruptGeneration() = remoteDataSource.interruptGeneration()
}
