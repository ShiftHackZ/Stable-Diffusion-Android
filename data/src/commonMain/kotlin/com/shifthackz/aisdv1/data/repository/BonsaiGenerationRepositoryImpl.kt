package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.mappers.mapBonsaiTextToImageResult
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.bonsai.BonsaiDiffusion
import com.shifthackz.aisdv1.domain.feature.bonsai.BonsaiModelSupport
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.BonsaiGenerationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implements `BonsaiGenerationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class BonsaiGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `bonsaiDiffusion` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val bonsaiDiffusion: BonsaiDiffusion,
    /**
     * Exposes the `downloadableLocalDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val downloadableLocalDataSource: DownloadableModelDataSource.Local,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : CoreGenerationRepository(
    mediaStoreGateway = mediaStoreGateway,
    localDataSource = localDataSource,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
), BonsaiGenerationRepository {

    override fun observeStatus() = bonsaiDiffusion.observeStatus()

    override suspend fun generateFromText(payload: TextToImagePayload) =
        getSelectedSupportedModel().let { model ->
            if (model.id == LocalAiModel.CustomBonsai.id) {
                generate(
                    payload = payload,
                    modelPath = preferenceManager.localBonsaiCustomModelPath,
                )
            } else if (model.downloaded) {
                generate(
                    payload = payload,
                    modelPath = model.resolveModelPath(),
                )
            } else {
                throw IllegalStateException("Model not downloaded.")
            }
        }

    override suspend fun interruptGeneration() = bonsaiDiffusion.interrupt()

    private suspend fun getSelectedSupportedModel(): LocalAiModel =
        downloadableLocalDataSource
            .getSelectedBonsai()
            .also { model ->
                if (!BonsaiModelSupport.isSupported(model)) {
                    throw IllegalStateException("Selected Bonsai model is not supported.")
                }
            }

    private fun LocalAiModel.resolveModelPath(): String =
        if (id == LocalAiModel.CustomBonsai.id) {
            preferenceManager.localBonsaiCustomModelPath
        } else {
            "${fileProviderDescriptor.localModelDirPath}/$id"
        }

    private suspend fun generate(
        payload: TextToImagePayload,
        modelPath: String,
    ) = withContext(Dispatchers.Default) {
        bonsaiDiffusion
            .process(
                payload = payload,
                modelPath = modelPath,
            )
            .let { base64 -> payload to base64 }
            .mapBonsaiTextToImageResult()
    }
        .let { result -> insertGenerationResult(result) }
}
