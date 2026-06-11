package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.data.core.CoreGenerationRepository
import com.shifthackz.aisdv1.data.mappers.mapCoreMlImageToImageResult
import com.shifthackz.aisdv1.data.mappers.mapCoreMlTextToImageResult
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.coreml.CoreMlDiffusion
import com.shifthackz.aisdv1.domain.feature.coreml.CoreMlModelSupport
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.CoreMlGenerationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implements `CoreMlGenerationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class CoreMlGenerationRepositoryImpl(
    mediaStoreGateway: MediaStoreGateway,
    localDataSource: GenerationResultDataSource.Local,
    backgroundWorkObserver: BackgroundWorkObserver,
    preferenceManager: PreferenceManager,
    /**
     * Exposes the `coreMlDiffusion` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val coreMlDiffusion: CoreMlDiffusion,
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
), CoreMlGenerationRepository {

    override fun observeStatus() = coreMlDiffusion.observeStatus()

    override suspend fun generateFromText(payload: TextToImagePayload) =
        getSelectedSupportedModel().let { model ->
            if (model.downloaded) {
                generate(
                    payload = payload,
                    modelPath = "${fileProviderDescriptor.localModelDirPath}/${model.id}",
                )
            } else {
                throw IllegalStateException("Model not downloaded.")
            }
        }

    override suspend fun generateFromImage(payload: ImageToImagePayload) =
        getSelectedSupportedModel().let { model ->
            if (model.downloaded) {
                generate(
                    payload = payload,
                    modelPath = "${fileProviderDescriptor.localModelDirPath}/${model.id}",
                )
            } else {
                throw IllegalStateException("Model not downloaded.")
            }
        }

    override suspend fun interruptGeneration() = coreMlDiffusion.interrupt()

    private suspend fun getSelectedSupportedModel(): LocalAiModel =
        downloadableLocalDataSource
            .getSelectedCoreMl()
            .also { model ->
                if (!CoreMlModelSupport.isSupported(model)) {
                    throw IllegalStateException("Selected Core ML model is not supported.")
                }
            }

    private suspend fun generate(
        payload: TextToImagePayload,
        modelPath: String,
    ) = withContext(Dispatchers.Default) {
        coreMlDiffusion
            .process(
                payload = payload,
                modelPath = modelPath,
            )
            .let { base64 -> payload to base64 }
            .mapCoreMlTextToImageResult()
    }
        .let { result -> insertGenerationResult(result) }

    private suspend fun generate(
        payload: ImageToImagePayload,
        modelPath: String,
    ) = withContext(Dispatchers.Default) {
        coreMlDiffusion
            .process(
                payload = payload,
                modelPath = modelPath,
            )
            .let { base64 -> payload to base64 }
            .mapCoreMlImageToImageResult()
    }
        .let { result -> insertGenerationResult(result) }
}
