package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.SdaiCloudInsufficientTokensException
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.SdaiCloudTopUpRepository
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationBenchmarkGate
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationGateResult
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationRequest
import com.shifthackz.aisdv1.feature.benchmark.isLocalGenerationSource
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
    private val dispatchersProvider: DispatchersProvider,
    private val textToImageUseCase: TextToImageUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val saveLastResultToCacheUseCase: SaveLastResultToCacheUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    private val preferenceManager: PreferenceManager,
    private val sdaiCloudTopUpRepository: SdaiCloudTopUpRepository,
    private val backgroundTaskManager: BackgroundTaskManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val wakeLockInterActor: WakeLockInterActor,
    private val platformServices: GenerationPlatformServices,
    private val buildInfoProvider: BuildInfoProvider,
    private val dimensionValidator: DimensionValidator,
    private val localGenerationBenchmarkGateProvider: () -> LocalGenerationBenchmarkGate,
    private val imageSaver: ImageSaver,
    private val imageSharer: ImageSharer,
    private val router: TextToImageRouter,
    private val currentState: () -> TextToImageState,
    private val emitState: (TextToImageState) -> Unit,
    private val updateState: ((TextToImageState) -> TextToImageState) -> Unit,
    private val launch: ViewModelLauncher,
    private val onError: (Throwable) -> Unit,
) {

    /**
     * Exposes the `generationJob` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private var generationJob: Job? = null

    private var pendingGeneration: PendingGeneration? = null

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
        if (validatedState.mode.isLocalGenerationSource()) {
            evaluateBenchmarkGate(validatedState, payload)
            return
        }

        startGeneration(validatedState, payload)
    }

    fun runBenchmarkFromPrompt() {
        localGenerationBenchmarkGateProvider().markFirstBenchmarkPromptAnswered()
        pendingGeneration = null
        updateState { it.copy(screenModal = GenerationModal.None) }
        router.navigateToBenchmark()
    }

    fun skipBenchmarkPrompt() {
        localGenerationBenchmarkGateProvider().markFirstBenchmarkPromptAnswered()
        val pending = pendingGeneration ?: return updateState { it.copy(screenModal = GenerationModal.None) }
        pendingGeneration = null
        updateState { it.copy(screenModal = GenerationModal.None) }
        startGeneration(pending.state, pending.payload)
    }

    fun continueAfterBenchmarkWarning(suppressFutureWarnings: Boolean) {
        if (suppressFutureWarnings) {
            localGenerationBenchmarkGateProvider().suppressRecommendationWarnings()
        }
        val pending = pendingGeneration ?: return updateState { it.copy(screenModal = GenerationModal.None) }
        pendingGeneration = null
        updateState { it.copy(screenModal = GenerationModal.None) }
        startGeneration(pending.state, pending.payload)
    }

    fun dismissBenchmarkDialog() {
        pendingGeneration = null
        updateState { it.copy(screenModal = GenerationModal.None) }
    }

    private fun evaluateBenchmarkGate(
        validatedState: TextToImageState,
        payload: TextToImagePayload,
    ) {
        pendingGeneration = PendingGeneration(validatedState, payload)
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { localGenerationBenchmarkGateProvider().evaluate(validatedState.toBenchmarkRequest(payload)) }
                .onSuccess { result ->
                    withContext(dispatchersProvider.immediate) {
                        when (result) {
                            LocalGenerationGateResult.Ready -> {
                                pendingGeneration = null
                                startGeneration(validatedState, payload)
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
                        pendingGeneration = null
                        startGeneration(validatedState, payload)
                    }
                }
        }
    }

    private fun startGeneration(
        validatedState: TextToImageState,
        payload: TextToImagePayload,
    ) {
        if (backgroundWorkObserver.hasActiveTasks()) {
            updateState {
                it.copy(screenModal = GenerationModal.Background.Running)
            }
            return
        }
        if (backgroundGenerationEnabled && validatedState.mode != ServerSource.SDAI_CLOUD) {
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
                wakeLockInterActor.acquireWakelockUseCase()
                try {
                    textToImageUseCase(payload).let { results ->
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
                    if (t is SdaiCloudInsufficientTokensException) {
                        pendingGeneration = PendingGeneration(validatedState, payload)
                        withContext(dispatchersProvider.immediate) {
                            updateState {
                                it.copy(
                                    generating = false,
                                    screenModal = GenerationModal.SdaiCloudTopUp.Required,
                                    results = emptyList(),
                                )
                            }
                        }
                        return@onFailure
                    }
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

    fun showSdaiCloudIapProducts() {
        updateState { it.copy(screenModal = GenerationModal.SdaiCloudTopUp.LoadingProducts) }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { sdaiCloudTopUpRepository.getIapProducts() }
                .onSuccess { products ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                screenModal = GenerationModal.SdaiCloudTopUp.PurchaseSheet(products),
                            )
                        }
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(screenModal = GenerationModal.Error(t.localizedMessageText()))
                        }
                    }
                    onError(t)
                }
        }
    }

    fun topUpSdaiCloudWithRewardedAd() {
        val pending = pendingGeneration ?: return updateState { it.copy(screenModal = GenerationModal.None) }
        topUpSdaiCloud(pending) { sdaiCloudTopUpRepository.topUpWithRewardedAd() }
    }

    fun topUpSdaiCloudWithIap(productId: String) {
        val pending = pendingGeneration
        if (pending == null) {
            topUpSdaiCloudOnly { sdaiCloudTopUpRepository.topUpWithIap(productId) }
        } else {
            topUpSdaiCloud(pending) { sdaiCloudTopUpRepository.topUpWithIap(productId) }
        }
    }

    fun restoreSdaiCloudIapPurchases() {
        val pending = pendingGeneration
        if (pending == null) {
            topUpSdaiCloudOnly { sdaiCloudTopUpRepository.restoreIapPurchases() }
        } else {
            topUpSdaiCloud(pending) { sdaiCloudTopUpRepository.restoreIapPurchases() }
        }
    }

    private fun topUpSdaiCloud(
        pending: PendingGeneration,
        block: suspend () -> com.shifthackz.aisdv1.domain.entity.SdaiCloudTokenBalance,
    ) {
        updateState { it.copy(screenModal = GenerationModal.SdaiCloudTopUp.Working) }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { block() }
                .onSuccess {
                    withContext(dispatchersProvider.immediate) {
                        pendingGeneration = null
                        startGeneration(pending.state, pending.payload)
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                generating = false,
                                screenModal = GenerationModal.Error(t.localizedMessageText()),
                            )
                        }
                    }
                    onError(t)
                }
        }
    }

    private fun topUpSdaiCloudOnly(
        block: suspend () -> com.shifthackz.aisdv1.domain.entity.SdaiCloudTokenBalance,
    ) {
        updateState { it.copy(screenModal = GenerationModal.SdaiCloudTopUp.Working) }
        launch(dispatchersProvider.io, CoroutineStart.DEFAULT) {
            runCatching { block() }
                .onSuccess {
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(screenModal = GenerationModal.None) }
                    }
                }
                .onFailure { t ->
                    if (t is CancellationException) throw t
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                generating = false,
                                screenModal = GenerationModal.Error(t.localizedMessageText()),
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

private data class PendingGeneration(
    val state: TextToImageState,
    val payload: TextToImagePayload,
)

private fun TextToImageState.toBenchmarkRequest(payload: TextToImagePayload): LocalGenerationRequest =
    LocalGenerationRequest(
        source = mode,
        width = payload.width,
        height = payload.height,
        samplingSteps = payload.samplingSteps,
        batchCount = batchCount,
        hiresFix = hires.enabled,
        sdxlBackend = payload.sdxlBackend,
    )
