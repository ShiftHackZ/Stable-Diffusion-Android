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

internal class TextToImageActionHandler(
    private val dispatchersProvider: DispatchersProvider,
    private val textToImageUseCase: TextToImageUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    private val preferenceManager: PreferenceManager,
    private val backgroundTaskManager: BackgroundTaskManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val platformServices: GenerationPlatformServices,
    private val buildInfoProvider: BuildInfoProvider,
    private val dimensionValidator: DimensionValidator,
    private val imageSaver: ImageSaver,
    private val imageSharer: ImageSharer,
    private val router: TextToImageRouter,
    private val currentState: () -> TextToImageState,
    private val emitState: (TextToImageState) -> Unit,
    private val updateState: ((TextToImageState) -> TextToImageState) -> Unit,
    private val launch: ViewModelLauncher,
    private val onError: (Throwable) -> Unit,
) {

    private var generationJob: Job? = null

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

    private val backgroundGenerationEnabled: Boolean
        get() = platformServices.supportsBackgroundGeneration && preferenceManager.backgroundGeneration

    private suspend fun persistResultsIfNeeded(results: List<AiGenerationResult>): List<AiGenerationResult> =
        results.map { persistResultIfNeeded(it) }

    private suspend fun persistResultIfNeeded(result: AiGenerationResult): AiGenerationResult =
        if (result.id > 0L) {
            result
        } else {
            result.copy(id = saveGenerationResultUseCase(result))
        }

    private suspend fun cacheResultIfNeeded(result: AiGenerationResult): AiGenerationResult =
        saveLastResultToCacheUseCase(result)
}
