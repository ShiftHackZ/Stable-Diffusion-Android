package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.ViewModelLauncher
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.navigation.router.ImageToImageRouter
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaveResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageShareResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSharer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class ImageToImageActionHandler(
    private val dispatchersProvider: DispatchersProvider,
    private val getRandomImageUseCase: GetRandomImageUseCase,
    private val imageToImageUseCase: ImageToImageUseCase,
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
    private val router: ImageToImageRouter,
    private val platformActions: ImageToImagePlatformActions,
    private val currentState: () -> ImageToImageState,
    private val emitState: (ImageToImageState) -> Unit,
    private val updateState: ((ImageToImageState) -> ImageToImageState) -> Unit,
    private val launch: ViewModelLauncher,
    private val onError: (Throwable) -> Unit,
) {

    private var generationJob: Job? = null

    fun pickImage(source: ImageToImagePickSource) {
        if (currentState().pickingImage || currentState().generating) return
        updateState {
            it.copy(
                pickingImage = true,
                error = null,
                message = null,
            )
        }
        launch(dispatchersProvider.immediate, CoroutineStart.DEFAULT) {
            when (val result = platformActions.pickImage(source)) {
                is ImageToImagePickResult.Selected -> updateState {
                    it.copy(
                        pickingImage = false,
                        imageBase64 = result.base64,
                        inPaint = ImageInPaintState(),
                        results = emptyList(),
                    )
                }
                ImageToImagePickResult.Cancelled -> updateState {
                    it.copy(pickingImage = false)
                }
                ImageToImagePickResult.Unsupported -> updateState {
                    it.copy(
                        pickingImage = false,
                        error = Localization.string("error_image_picking_unavailable"),
                    )
                }
                is ImageToImagePickResult.Failed -> updateState {
                    it.copy(
                        pickingImage = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun pickRandomImage() {
        if (currentState().pickingImage || currentState().generating) return
        updateState {
            it.copy(
                pickingImage = true,
                error = null,
                message = null,
            )
        }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { cropBase64ImageToSquare(Base64.encode(getRandomImageUseCase())) }
                .onSuccess { base64 ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                pickingImage = false,
                                imageBase64 = base64,
                                inPaint = ImageInPaintState(),
                                results = emptyList(),
                            )
                        }
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                pickingImage = false,
                                error = t.message ?: Localization.string("error_invalid"),
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    fun generate() {
        if (currentState().generating) return
        val validatedState = currentState().validated(dimensionValidator)
        emitState(validatedState)
        if (!validatedState.canGenerate) return

        if (backgroundWorkObserver.hasActiveTasks()) {
            updateState {
                it.copy(screenModal = GenerationModal.Background.Running)
            }
            return
        }

        val shouldScheduleBackground = backgroundGenerationEnabled
        updateState {
            it.copy(
                generating = true,
                error = null,
                message = null,
                screenModal = if (shouldScheduleBackground) {
                    GenerationModal.None
                } else {
                    validatedState.progressModal(preferenceManager.localOnnxAllowCancel)
                },
                results = emptyList(),
            )
        }
        generationJob?.cancel()
        generationJob = launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching {
                val maskBase64 = validatedState.inPaint
                    .takeIf(ImageInPaintState::hasMask)
                    ?.let { encodeInPaintMaskBase64(validatedState.imageBase64, it.strokes) }
                    ?: ""
                if (validatedState.inPaint.hasMask && maskBase64.isBlank()) {
                    error(Localization.string("error_invalid"))
                }
                val payload = validatedState.mapToPayload(maskBase64)
                if (shouldScheduleBackground) {
                    backgroundTaskManager.scheduleImageToImageTask(payload)
                    backgroundWorkObserver.refreshStatus()
                    return@runCatching emptyList()
                }
                platformServices.acquireWakeLock()
                try {
                    imageToImageUseCase(payload).let { results ->
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
                    if (shouldScheduleBackground) {
                        withContext(dispatchersProvider.immediate) {
                            updateState {
                                it.copy(
                                    generating = false,
                                    screenModal = GenerationModal.Background.Scheduled,
                                )
                            }
                        }
                        return@onSuccess
                    }
                    platformServices.showGenerationSucceeded()
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                generating = false,
                                screenModal = results.toImageToImageResultModal(
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
                                message = Localization.string("success"),
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
        updateState { it.copy(savingImage = true, error = null, message = null) }
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
                            message = Localization.string("message_image_saved"),
                        )
                        ImageSaveResult.Unsupported -> it.copy(
                            savingImage = false,
                            error = Localization.string("error_image_saving_unavailable"),
                        )
                        is ImageSaveResult.Failed -> it.copy(savingImage = false, error = result.message)
                    }
                }
            }
        }
    }

    fun shareImage(base64: String) {
        if (currentState().savingImage || currentState().sharingImage) return
        updateState { it.copy(sharingImage = true, error = null, message = null) }
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
                            message = Localization.string("message_share_sheet_opened"),
                        )
                        ImageShareResult.Unsupported -> it.copy(
                            sharingImage = false,
                            error = Localization.string("error_image_sharing_unavailable"),
                        )
                        is ImageShareResult.Failed -> it.copy(sharingImage = false, error = result.message)
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
