package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationBenchmarkGate
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationGateResult
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationRequest
import com.shifthackz.aisdv1.feature.benchmark.isLocalGenerationSource
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

/**
 * Coordinates `ImageToImageActionHandler` behavior in the SDAI presentation layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
internal class ImageToImageActionHandler(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `getRandomImageUseCase` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val getRandomImageUseCase: GetRandomImageUseCase,
    /**
     * Exposes the `imageToImageUseCase` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val imageToImageUseCase: ImageToImageUseCase,
    /**
     * Exposes the `saveGenerationResultUseCase` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    /**
     * Exposes the `saveLastResultToCacheUseCase` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    /**
     * Exposes the `interruptGenerationUseCase` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    /**
     * Exposes the `preferenceManager` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `backgroundTaskManager` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val backgroundTaskManager: BackgroundTaskManager,
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `wakeLockInterActor` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val wakeLockInterActor: WakeLockInterActor,
    /**
     * Exposes the `platformServices` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val platformServices: GenerationPlatformServices,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `dimensionValidator` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val dimensionValidator: DimensionValidator,
    /**
     * Exposes the `localGenerationBenchmarkGateProvider` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val localGenerationBenchmarkGateProvider: () -> LocalGenerationBenchmarkGate,
    /**
     * Exposes the `imageSaver` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val imageSaver: ImageSaver,
    /**
     * Exposes the `imageSharer` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val imageSharer: ImageSharer,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val router: ImageToImageRouter,
    /**
     * Exposes the `platformActions` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val platformActions: ImageToImagePlatformActions,
    /**
     * Exposes the `currentState` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val currentState: () -> ImageToImageState,
    /**
     * Exposes the `emitState` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val emitState: (ImageToImageState) -> Unit,
    /**
     * Exposes the `updateState` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val updateState: ((ImageToImageState) -> ImageToImageState) -> Unit,
    /**
     * Exposes the `launch` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
    private val launch: ViewModelLauncher,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
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

    private var pendingGenerationState: ImageToImageState? = null

    /**
     * Executes the `pickImage` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `pickRandomImage` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `generate` step in the SDAI presentation layer.
     *
     * @throws IllegalStateException when the current state is invalid.
     * @author Dmitriy Moroz
     */
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

        if (validatedState.mode.isLocalGenerationSource()) {
            evaluateBenchmarkGate(validatedState)
            return
        }

        startGeneration(validatedState)
    }

    fun runBenchmarkFromPrompt() {
        localGenerationBenchmarkGateProvider().markFirstBenchmarkPromptAnswered()
        pendingGenerationState = null
        updateState { it.copy(screenModal = GenerationModal.None) }
        router.navigateToBenchmark()
    }

    fun skipBenchmarkPrompt() {
        localGenerationBenchmarkGateProvider().markFirstBenchmarkPromptAnswered()
        val pending = pendingGenerationState ?: return updateState { it.copy(screenModal = GenerationModal.None) }
        pendingGenerationState = null
        updateState { it.copy(screenModal = GenerationModal.None) }
        startGeneration(pending)
    }

    fun continueAfterBenchmarkWarning(suppressFutureWarnings: Boolean) {
        if (suppressFutureWarnings) {
            localGenerationBenchmarkGateProvider().suppressRecommendationWarnings()
        }
        val pending = pendingGenerationState ?: return updateState { it.copy(screenModal = GenerationModal.None) }
        pendingGenerationState = null
        updateState { it.copy(screenModal = GenerationModal.None) }
        startGeneration(pending)
    }

    fun dismissBenchmarkDialog() {
        pendingGenerationState = null
        updateState { it.copy(screenModal = GenerationModal.None) }
    }

    private fun evaluateBenchmarkGate(validatedState: ImageToImageState) {
        pendingGenerationState = validatedState
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            val requestPayload = validatedState.mapToPayload()
            runCatching { localGenerationBenchmarkGateProvider().evaluate(validatedState.toBenchmarkRequest(requestPayload)) }
                .onSuccess { result ->
                    withContext(dispatchersProvider.immediate) {
                        when (result) {
                            LocalGenerationGateResult.Ready -> {
                                pendingGenerationState = null
                                startGeneration(validatedState)
                            }
                            LocalGenerationGateResult.AskRunBenchmark -> updateState {
                                it.copy(screenModal = GenerationModal.Benchmark.FirstLocalGeneration)
                            }
                            is LocalGenerationGateResult.ConfirmExceedsRecommendation -> updateState {
                                it.copy(
                                    screenModal = GenerationModal.Benchmark.ExceedsRecommendation(
                                        reasons = result.reasons,
                                        recommendation = result.recommendation,
                                    ),
                                )
                            }
                        }
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    onError(t)
                    withContext(dispatchersProvider.immediate) {
                        pendingGenerationState = null
                        startGeneration(validatedState)
                    }
                }
        }
    }

    private fun startGeneration(validatedState: ImageToImageState) {
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
                    .takeIf { validatedState.sourceSupportsInPaint }
                    ?.takeIf(ImageInPaintState::hasMask)
                    ?.let { encodeInPaintMaskBase64(validatedState.imageBase64, it.strokes) }
                    ?: ""
                if (
                    validatedState.sourceSupportsInPaint &&
                    validatedState.inPaint.hasMask &&
                    maskBase64.isBlank()
                ) {
                    error(Localization.string("error_invalid"))
                }
                val payload = validatedState.mapToPayload(maskBase64)
                if (shouldScheduleBackground) {
                    backgroundTaskManager.scheduleImageToImageTask(payload)
                    backgroundWorkObserver.refreshStatus()
                    return@runCatching emptyList()
                }
                wakeLockInterActor.acquireWakelockUseCase()
                try {
                    imageToImageUseCase(payload).let { results ->
                        if (preferenceManager.autoSaveAiResults) {
                            persistResultsIfNeeded(results)
                        } else {
                            results
                        }
                    }
                } finally {
                    wakeLockInterActor.releaseWakeLockUseCase()
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

    /**
     * Performs the SDAI side effect handled by `shareImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
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

private fun ImageToImageState.toBenchmarkRequest(payload: ImageToImagePayload): LocalGenerationRequest =
    LocalGenerationRequest(
        source = mode,
        width = payload.width,
        height = payload.height,
        samplingSteps = payload.samplingSteps,
        batchCount = payload.batchCount,
    )
