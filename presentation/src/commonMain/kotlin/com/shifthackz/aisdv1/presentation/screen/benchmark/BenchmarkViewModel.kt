package com.shifthackz.aisdv1.presentation.screen.benchmark

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkManager
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkProviderIssue
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkProviderRecommendation
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkResult
import com.shifthackz.aisdv1.presentation.navigation.router.BenchmarkRouter
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

/**
 * Coordinates benchmark screen state and safe benchmark execution.
 *
 * The ViewModel never invokes model inference. It delegates to [BenchmarkManager],
 * which only reads hardware metadata and runs bounded synthetic CPU/memory work.
 *
 * @author Dmitriy Moroz
 */
class BenchmarkViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val benchmarkManager: BenchmarkManager,
    private val router: BenchmarkRouter,
    private val platformActions: BenchmarkPlatformActions,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<BenchmarkState, BenchmarkIntent, EmptyEffect>(
    initialState = BenchmarkState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        observeBenchmarkData()
        refreshDevice()
    }

    override fun processIntent(intent: BenchmarkIntent) {
        when (intent) {
            BenchmarkIntent.NavigateBack -> router.navigateBack()
            BenchmarkIntent.RunBenchmark -> runBenchmark()
            BenchmarkIntent.ShareResults -> shareResults()
            BenchmarkIntent.DismissError -> updateState { it.copy(error = null) }
        }
    }

    private fun observeBenchmarkData() {
        launch(dispatchersProvider.io) {
            benchmarkManager.observeLatest()
                .catch { throwable -> handleFailure(throwable) }
                .collect { latest ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loadingResult = false,
                                latestResult = latest,
                            )
                        }
                    }
                }
        }
    }

    private fun refreshDevice() {
        updateState { it.copy(loadingDevice = true, error = null) }
        launch(dispatchersProvider.io) {
            runCatching { benchmarkManager.inspectDevice() }
                .onSuccess { deviceInfo ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loadingDevice = false,
                                deviceInfo = deviceInfo,
                            )
                        }
                    }
                }
                .onFailure { throwable ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loadingDevice = false,
                                error = throwable.localizedMessage(),
                            )
                        }
                    }
                    onError(throwable)
                }
        }
    }

    private fun runBenchmark() {
        if (currentState.running) return
        updateState { it.copy(running = true, error = null) }
        launch(dispatchersProvider.io) {
            runCatching { benchmarkManager.runBenchmark() }
                .onSuccess { result ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                running = false,
                                loadingDevice = false,
                                deviceInfo = result.deviceInfo,
                                latestResult = result,
                            )
                        }
                    }
                }
                .onFailure { throwable ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                running = false,
                                error = throwable.localizedMessage(),
                            )
                        }
                    }
                    onError(throwable)
                }
        }
    }

    private fun shareResults() {
        val latest = currentState.latestResult ?: return
        launch(dispatchersProvider.io) {
            when (val result = platformActions.shareText(latest.shareText())) {
                BenchmarkActionResult.Done -> Unit
                BenchmarkActionResult.Unsupported -> withContext(dispatchersProvider.immediate) {
                    updateState { it.copy(error = Localization.string("error_generic")) }
                }
                is BenchmarkActionResult.Failed -> withContext(dispatchersProvider.immediate) {
                    updateState { it.copy(error = result.message) }
                }
            }
        }
    }

    private suspend fun handleFailure(throwable: Throwable) {
        withContext(dispatchersProvider.immediate) {
            updateState {
                it.copy(
                    loadingResult = false,
                    error = throwable.localizedMessage(),
                )
            }
        }
        onError(throwable)
    }

    private fun Throwable.localizedMessage(): String =
        message ?: Localization.string("benchmark_error_generic")
}

private fun BenchmarkResult.shareText(): String = buildString {
    appendLine(Localization.string("title_benchmark"))
    appendLine("${Localization.string("benchmark_score")}: $totalScore")
    appendLine("${Localization.string("benchmark_device")}: ${deviceInfo.deviceName()}")
    appendLine("${Localization.string("benchmark_cpu")}: ${deviceInfo.cpuName}")
    appendLine("${Localization.string("benchmark_gpu")}: ${deviceInfo.gpuName}")
    appendLine("${Localization.string("benchmark_ram")}: ${Localization.string("benchmark_mb", deviceInfo.totalRamMb)}")
    appendLine("${Localization.string("benchmark_estimated_time_short")}: ${Localization.string("benchmark_seconds", estimatedTimeSeconds)}")
    providerRecommendations.forEach { recommendation ->
        appendLine()
        appendLine("${recommendation.provider.displayName()} ${Localization.string("benchmark_recommendations")}")
        if (recommendation.recommended) {
            appendLine("${Localization.string("benchmark_recommended_size")}: ${recommendation.width} x ${recommendation.height}")
            appendLine("${Localization.string("benchmark_recommended_steps")}: ${recommendation.samplingSteps}")
            appendLine("${Localization.string("benchmark_recommended_cfg")}: ${recommendation.cfgScale}")
            appendLine("${Localization.string("benchmark_recommended_background")}: ${recommendation.backgroundLabel()}")
            if (recommendation.provider == ServerSource.LOCAL_STABLE_DIFFUSION_CPP) {
                appendLine("${Localization.string("benchmark_recommended_backend")}: ${recommendation.sdxlBackend.displayName()}")
            }
        } else {
            appendLine(Localization.string("benchmark_provider_not_recommended"))
            recommendation.issues.forEach { issue ->
                appendLine("- ${issue.localizedText()}")
            }
        }
    }
}

private fun BenchmarkProviderRecommendation.backgroundLabel(): String =
    if (backgroundGeneration) {
        Localization.string("benchmark_recommended")
    } else {
        Localization.string("benchmark_not_recommended")
    }

private fun BenchmarkProviderIssue.localizedText(): String = when (this) {
    BenchmarkProviderIssue.PLATFORM_UNSUPPORTED ->
        Localization.string("benchmark_issue_platform_unsupported")
    BenchmarkProviderIssue.LOW_MEMORY ->
        Localization.string("benchmark_issue_low_memory")
    BenchmarkProviderIssue.LOW_SCORE ->
        Localization.string("benchmark_issue_low_score")
    BenchmarkProviderIssue.ONNX_SLOW_LOW_MEMORY ->
        Localization.string("benchmark_issue_onnx_slow_low_memory")
    BenchmarkProviderIssue.MEDIAPIPE_UNSTABLE_LOW_MEMORY ->
        Localization.string("benchmark_issue_mediapipe_unstable_low_memory")
    BenchmarkProviderIssue.SDXL_TINY_EXPERIMENTAL_ONLY ->
        Localization.string("benchmark_issue_sdxl_tiny_experimental_only")
    BenchmarkProviderIssue.SDXL_BACKEND_NOT_VALIDATED ->
        Localization.string("benchmark_issue_sdxl_backend_not_validated")
    BenchmarkProviderIssue.ACCELERATOR_API_NOT_AVAILABLE ->
        Localization.string("benchmark_issue_accelerator_api_not_available")
}

private fun ServerSource.displayName(): String = when (this) {
    ServerSource.AUTOMATIC1111 -> Localization.string("srv_type_own_short")
    ServerSource.SWARM_UI -> Localization.string("srv_type_swarm_ui")
    ServerSource.LOCAL_MICROSOFT_ONNX -> Localization.string("srv_type_local_short")
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Localization.string("srv_type_media_pipe_short")
    ServerSource.LOCAL_STABLE_DIFFUSION_CPP -> Localization.string("srv_type_sdxl_short")
    ServerSource.LOCAL_APPLE_CORE_ML -> "Core ML"
    ServerSource.HORDE -> Localization.string("srv_type_horde_short")
    ServerSource.HUGGING_FACE -> Localization.string("srv_type_hugging_face_short")
    ServerSource.OPEN_AI -> Localization.string("srv_type_open_ai")
    ServerSource.STABILITY_AI -> Localization.string("srv_type_stability_ai")
    ServerSource.FAL_AI -> Localization.string("srv_type_fal_ai")
}

private fun SdxlBackend.displayName(): String = displayName

private fun com.shifthackz.aisdv1.feature.benchmark.BenchmarkDeviceInfo.deviceName(): String =
    listOf(manufacturer, model)
        .filter(String::isNotBlank)
        .joinToString(" ")
        .ifBlank { Localization.string("benchmark_unknown") }
