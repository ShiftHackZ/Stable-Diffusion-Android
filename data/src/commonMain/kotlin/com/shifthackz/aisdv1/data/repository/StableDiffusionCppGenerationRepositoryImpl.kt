package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.local.DownloadableModelFileStore
import com.shifthackz.aisdv1.data.mappers.mapStableDiffusionCppTextToImageResult
import com.shifthackz.aisdv1.data.mappers.withModelName
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.sdxl.StableDiffusionCpp
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionCppGenerationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class StableDiffusionCppGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    preferenceManager: PreferenceManager,
    private val stableDiffusionCpp: StableDiffusionCpp,
    private val downloadableLocalDataSource: DownloadableModelDataSource.Local,
    private val modelFileStore: DownloadableModelFileStore,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), StableDiffusionCppGenerationRepository {

    private val sdxlPreferenceManager = preferenceManager

    override fun observeStatus() = stableDiffusionCpp.observeStatus()

    override suspend fun generateFromText(payload: TextToImagePayload) =
        downloadableLocalDataSource.getSelectedSdxl().let { model ->
            if (model.downloaded) {
                generate(
                    payload = payload,
                    modelPath = model.resolveSdxlPath(),
                    modelName = model.name.ifBlank { model.id },
                )
            } else {
                throw IllegalStateException("Model not downloaded.")
            }
        }

    override suspend fun interruptGeneration() = stableDiffusionCpp.interrupt()

    private fun LocalAiModel.resolveSdxlPath(): String =
        if (id == LocalAiModel.CustomSdxl.id) {
            modelFileStore.resolveSingleFilePath(sdxlPreferenceManager.localSdxlCustomModelPath)
        } else {
            modelFileStore.resolvePath(this)
        }

    private suspend fun generate(
        payload: TextToImagePayload,
        modelPath: String,
        modelName: String,
    ) = withContext(Dispatchers.Default) {
        stableDiffusionCpp
            .process(
                payload = payload,
                modelPath = modelPath,
            )
            .let { base64 -> payload to base64 }
            .mapStableDiffusionCppTextToImageResult()
            .withModelName(modelName)
    }
        .let { result -> insertGenerationResult(result) }
}
