package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.ViewModelLauncher
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * Coordinates `TextToImageActionHandler` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class TextToImageActionHandler(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `textToImageUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val textToImageUseCase: TextToImageUseCase,
    /**
     * Exposes the `saveGenerationResultUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    /**
     * Exposes the `saveLastResultToCacheUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    /**
     * Exposes the `interruptGenerationUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    /**
     * Exposes the `preferenceManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `backgroundTaskManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundTaskManager: BackgroundTaskManager,
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `platformServices` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val platformServices: GenerationPlatformServices,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `dimensionValidator` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dimensionValidator: DimensionValidator,
    /**
     * Exposes the `imageSaver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSaver: ImageSaver,
    /**
     * Exposes the `imageSharer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSharer: ImageSharer,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: TextToImageRouter,
    /**
     * Exposes the `currentState` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val currentState: () -> TextToImageState,
    /**
     * Exposes the `emitState` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val emitState: (TextToImageState) -> Unit,
    /**
     * Exposes the `updateState` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val updateState: ((TextToImageState) -> TextToImageState) -> Unit,
    /**
     * Exposes the `launch` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val launch: ViewModelLauncher,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit,
) {

    /**
     * Exposes the `generationJob` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private var generationJob: Job? = null

    /**
     * Executes the `generate` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun generate() {
        if (currentState().generating) return
        val validatedState = currentState().validated(dimensionValidator)
        emitState(validatedState)
        if (!validatedState.canGenerate()) return

        val payload = validatedState.mapToPayload()
        if (backgroundWorkObserver.hasActiveTasks()) {
            updateState {
                it.copy(screenModal = GenerationModal.Background.Running)
            }
            return
        }
        if (backgroundGenerationEnabled) {
            backgroundTaskManager.scheduleTextToImageTask(payload)
            backgroundWorkObserver.refreshStatus()
            updateState {
                it.copy(screenModal = GenerationModal.Background.Scheduled)
            }
            return
        }

        updateState {
            it.copy(
                generating = true,
                error = null,
                message = null,
                screenModal = validatedState.progressModal(preferenceManager.localOnnxAllowCancel),
                results = emptyList(),
            )
        }
        generationJob?.cancel()
        generationJob = launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching {
                platformServices.acquireWakeLock()
                try {
                    textToImageUseCase(payload).let { results ->
                        if (preferenceManager.autoSaveAiResults) {
                            persistResultsIfNeeded(results)
                        } else {
                            results
                        }
                    }
                } finally {
                    platformServices.releaseWakeLock()
                }
            }
                .onSuccess { results ->
                    platformServices.showGenerationSucceeded()
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                generating = false,
                                screenModal = results.toTextToImageResultModal(
                                    autoSaveEnabled = preferenceManager.autoSaveAiResults,
                                    reportEnabled = buildInfoProvider.type != BuildType.FOSS,
                                ),
                                results = emptyList(),
                            )
                        }
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    platformServices.showGenerationFailed()
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                generating = false,
                                screenModal = GenerationModal.Error(t.localizedMessageText()),
                                results = emptyList(),
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    /**
     * Executes the `cancelGeneration` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
        updateState {
            it.copy(
                generating = false,
                screenModal = GenerationModal.None,
            )
        }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { interruptGenerationUseCase() }
                .onFailure(onError)
        }
    }

    /**
     * Performs the SDAI side effect handled by `saveGenerationResults`.
     *
     * @param results results value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun saveGenerationResults(results: List<AiGenerationResult>) {
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { persistResultsIfNeeded(results) }
                .onSuccess {
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                screenModal = GenerationModal.None,
                                message = Localization.string("success").asUiText(),
                            )
                        }
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(screenModal = GenerationModal.Error(t.localizedMessageText())) }
                    }
                    onError(t)
                }
        }
    }

    /**
     * Executes the `viewGenerationResult` step in the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun viewGenerationResult(result: AiGenerationResult) {
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { cacheResultIfNeeded(result) }
                .onSuccess { cached ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(screenModal = GenerationModal.None) }
                        router.navigateToGalleryDetails(cached.id)
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(screenModal = GenerationModal.Error(t.localizedMessageText())) }
                    }
                    onError(t)
                }
        }
    }

    /**
     * Executes the `reportGenerationResult` step in the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun reportGenerationResult(result: AiGenerationResult) {
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { cacheResultIfNeeded(result) }
                .onSuccess { cached ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(screenModal = GenerationModal.None) }
                        router.navigateToReportImage(cached.id)
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(screenModal = GenerationModal.Error(t.localizedMessageText())) }
                    }
                    onError(t)
                }
        }
    }

    /**
     * Performs the SDAI side effect handled by `saveImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    fun saveImage(base64: String) {
        if (currentState().savingImage || currentState().sharingImage) return
        updateState {
            it.copy(
                savingImage = true,
                error = null,
                message = null,
            )
        }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            val result = runCatching { imageSaver.save(base64) }
                .getOrElse { t ->
                    if (t is CancellationException) throw t
                    ImageSaveResult.Failed(t.message ?: Localization.string("error_unable_to_save_image"))
                }
            withContext(dispatchersProvider.immediate) {
                updateState {
                    when (result) {
                        ImageSaveResult.Saved -> it.copy(
                            savingImage = false,
                            message = Localization.string("success").asUiText(),
                        )
                        ImageSaveResult.Unsupported -> it.copy(
                            savingImage = false,
                            error = Localization.string("error_image_saving_unavailable").asUiText(),
                        )
                        is ImageSaveResult.Failed -> it.copy(
                            savingImage = false,
                            error = result.message.asUiText(),
                        )
                    }
                }
            }
        }
    }

    /**
     * Performs the SDAI side effect handled by `shareImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    fun shareImage(base64: String) {
        if (currentState().savingImage || currentState().sharingImage) return
        updateState {
            it.copy(
                sharingImage = true,
                error = null,
                message = null,
            )
        }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            val result = runCatching { imageSharer.share(base64) }
                .getOrElse { t ->
                    if (t is CancellationException) throw t
                    ImageShareResult.Failed(t.message ?: Localization.string("error_unable_to_share_image"))
                }
            withContext(dispatchersProvider.immediate) {
                updateState {
                    when (result) {
                        ImageShareResult.Sent -> it.copy(
                            sharingImage = false,
                            message = Localization.string("success").asUiText(),
                        )
                        ImageShareResult.Unsupported -> it.copy(
                            sharingImage = false,
                            error = Localization.string("error_image_sharing_unavailable").asUiText(),
                        )
                        is ImageShareResult.Failed -> it.copy(
                            sharingImage = false,
                            error = result.message.asUiText(),
                        )
                    }
                }
            }
        }
    }

    /**
     * Exposes the `backgroundGenerationEnabled` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundGenerationEnabled: Boolean
        get() = platformServices.supportsBackgroundGeneration && preferenceManager.backgroundGeneration

    /**
     * Executes the `persistResultsIfNeeded` step in the SDAI presentation layer.
     *
     * @param results results value consumed by the API.
     * @return Result produced by `persistResultsIfNeeded`.
     * @author Dmitriy Moroz
     */
    private suspend fun persistResultsIfNeeded(results: List<AiGenerationResult>): List<AiGenerationResult> =
        results.map { persistResultIfNeeded(it) }

    /**
     * Executes the `persistResultIfNeeded` step in the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `persistResultIfNeeded`.
     * @author Dmitriy Moroz
     */
    private suspend fun persistResultIfNeeded(result: AiGenerationResult): AiGenerationResult =
        if (result.id > 0L) {
            result
        } else {
            result.copy(id = saveGenerationResultUseCase(result))
        }

    /**
     * Executes the `cacheResultIfNeeded` step in the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `cacheResultIfNeeded`.
     * @author Dmitriy Moroz
     */
    private suspend fun cacheResultIfNeeded(result: AiGenerationResult): AiGenerationResult =
        saveLastResultToCacheUseCase(result)
}
