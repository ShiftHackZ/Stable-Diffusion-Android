package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
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
import io.reactivex.rxjava3.core.Single

internal class LocalDiffusionGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    private val preferenceManager: PreferenceManager,
    private val localDiffusion: LocalDiffusion,
    private val downloadableLocalDataSource: DownloadableModelDataSource.Local,
    private val bitmapToBase64Converter: BitmapToBase64Converter,
    private val schedulersProvider: SchedulersProvider,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    base64ToBitmapConverter = base64ToBitmapConverter,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), LocalDiffusionGenerationRepository {

    override fun observeStatus() = localDiffusion.observeStatus()

    override fun generateFromText(payload: TextToImagePayload) = downloadableLocalDataSource
        .getSelected()
        .flatMap { model ->
            if (model.downloaded) generate(payload)
            else Single.error(IllegalStateException("Model not downloaded."))
        }

    override fun interruptGeneration() = localDiffusion.interrupt()

    private fun generate(payload: TextToImagePayload) = localDiffusion
        .process(payload)
        .subscribeOn(schedulersProvider.byToken(preferenceManager.localDiffusionSchedulerThread))
        .map(BitmapToBase64Converter::Input)
        .flatMap(bitmapToBase64Converter::invoke)
        .map(BitmapToBase64Converter.Output::base64ImageString)
        .map { base64 -> payload to base64 }
        .map(Pair<TextToImagePayload, String>::mapLocalDiffusionToAiGenResult)
        .flatMap(::insertGenerationResult)
}
