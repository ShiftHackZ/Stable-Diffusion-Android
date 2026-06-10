package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.mappers.mapLocalDiffusionToAiGenResult
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class LocalDiffusionGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val preferenceManager: PreferenceManager,
    private val localDiffusion: LocalDiffusion,
    private val downloadableLocalDataSource: DownloadableModelDataSource.Local,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), LocalDiffusionGenerationRepository {

    override fun observeStatus() = localDiffusion.observeStatus()

    override suspend fun generateFromText(payload: TextToImagePayload) =
        if (downloadableLocalDataSource.getSelectedOnnx().downloaded) {
            generate(payload)
        } else {
            throw IllegalStateException("Model not downloaded.")
        }

    override suspend fun interruptGeneration() = localDiffusion.interrupt()

    private suspend fun generate(payload: TextToImagePayload) = withContext(Dispatchers.Default) {
        localDiffusion
            .process(payload)
            .let(BitmapToBase64Converter::Input)
            .let(bitmapToBase64Converter::invoke)
            .base64ImageString
            .let { base64 -> payload to base64 }
            .mapLocalDiffusionToAiGenResult()
    }
        .let { result -> insertGenerationResult(result) }
}
