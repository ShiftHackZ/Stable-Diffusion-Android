package com.shifthackz.aisdv1.presentation.screen.networkusage

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.usecase.settings.ObserveNetworkUsageUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ResetNetworkUsageUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.NetworkUsageRouter
import com.shifthackz.aisdv1.presentation.screen.networkusage.model.NetworkUsageIntent
import com.shifthackz.aisdv1.presentation.screen.networkusage.model.NetworkUsageState
import com.shifthackz.aisdv1.presentation.model.UsageCategory
import com.shifthackz.aisdv1.presentation.model.UsageItem
import com.shifthackz.aisdv1.presentation.model.UsageState
import com.shifthackz.aisdv1.presentation.model.resolveSelectedCategory
import kotlinx.coroutines.flow.catch

/**
 * ViewModel for the standalone network usage screen.
 *
 * It subscribes to persisted traffic counters through [ObserveNetworkUsageUseCase] so the screen
 * updates when Ktor calls or model downloads report bytes. Resetting statistics clears Room data
 * and briefly switches to the shimmer state while the new zero snapshot is emitted.
 *
 * @param dispatchersProvider App coroutine dispatchers used by the MVI base class and IO work.
 * @param observeNetworkUsageUseCase Live Room-backed traffic counter stream.
 * @param resetNetworkUsageUseCase Use case that clears all persisted network counters.
 * @param router Standalone route navigation contract.
 * @param onError Error callback forwarded to the app-level error handling pipeline.
 *
 * @author Dmitriy Moroz
 */
class NetworkUsageViewModel(
    dispatchersProvider: DispatchersProvider,
    private val observeNetworkUsageUseCase: ObserveNetworkUsageUseCase,
    private val resetNetworkUsageUseCase: ResetNetworkUsageUseCase,
    private val router: NetworkUsageRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<NetworkUsageState, NetworkUsageIntent, EmptyEffect>(
    initialState = NetworkUsageState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    private val ioDispatcher = dispatchersProvider.io

    init {
        observeNetworkUsage()
    }

    override fun processIntent(intent: NetworkUsageIntent) {
        when (intent) {
            NetworkUsageIntent.NavigateBack -> router.navigateBack()

            is NetworkUsageIntent.SelectCategory -> selectCategory(intent.category)

            NetworkUsageIntent.ResetStatistics -> resetStatistics()
        }
    }

    private fun selectCategory(category: UsageCategory) {
        updateState { state ->
            state.copy(usage = state.usage.copy(selectedCategory = category))
        }
    }

    private fun observeNetworkUsage() {
        updateState { state ->
            state.copy(usage = state.usage.copy(loading = true))
        }
        launch(ioDispatcher) {
            observeNetworkUsageUseCase()
                .catch { t ->
                    onError(t)
                    updateState { state ->
                        state.copy(usage = state.usage.copy(loading = false))
                    }
                }
                .collect { usage ->
                    updateState { state ->
                        state.copy(
                            usage = usage.toUsageState(
                                selectedCategory = state.usage.selectedCategory,
                            ),
                        )
                    }
                }
        }
    }

    private fun resetStatistics() {
        updateState { state ->
            state.copy(usage = state.usage.copy(loading = true))
        }
        launch(ioDispatcher) {
            runCatching {
                resetNetworkUsageUseCase()
            }
                .onSuccess {
                    updateState { state ->
                        state.copy(
                            usage = NetworkUsage().toUsageState(
                                selectedCategory = state.usage.selectedCategory,
                            ),
                        )
                    }
                }
                .onFailure { t ->
                    onError(t)
                    updateState { state ->
                        state.copy(usage = state.usage.copy(loading = false))
                    }
                }
        }
    }
}

private fun NetworkUsage.toUsageState(
    selectedCategory: UsageCategory?,
): UsageState {
    val items = listOf(
        UsageItem(
            category = UsageCategory.TRAFFIC_MODELS,
            bytes = modelDownloadBytes,
        ),
        UsageItem(
            category = UsageCategory.TRAFFIC_CONFIGS,
            bytes = configBytes,
        ),
        UsageItem(
            category = UsageCategory.TRAFFIC_INFERENCE,
            bytes = inferenceBytes,
        ),
    )
    return UsageState(
        loading = false,
        items = items,
        selectedCategory = items.resolveSelectedCategory(selectedCategory),
    )
}
