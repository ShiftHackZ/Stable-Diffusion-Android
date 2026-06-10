package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.core.mvi.EmptyIntent
import com.shifthackz.aisdv1.domain.usecase.connectivity.GetMonitorConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveMonitorConnectivityUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

/**
 * Coordinates `ConnectivityViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class ConnectivityViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `observeServerConnectivityUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeServerConnectivityUseCase: ObserveSeverConnectivityUseCase,
    /**
     * Exposes the `getMonitorConnectivityUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getMonitorConnectivityUseCase: GetMonitorConnectivityUseCase,
    /**
     * Exposes the `observeMonitorConnectivityUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeMonitorConnectivityUseCase: ObserveMonitorConnectivityUseCase,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<ConnectivityState, EmptyIntent, EmptyEffect>(
    initialState = ConnectivityState.Uninitialized(
        enabled = getMonitorConnectivityUseCase(),
    ),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.io) {
            combine(
                observeServerConnectivityUseCase(),
                observeMonitorConnectivityUseCase()
                    .onStart { emit(getMonitorConnectivityUseCase()) }
                    .distinctUntilChanged(),
            ) { connected, enabled ->
                ConnectivityState.consume(connected to enabled)
            }
                .distinctUntilChanged()
                .catch { onError(it) }
                .collect { state ->
                    withContext(dispatchersProvider.immediate) {
                        emitState(state)
                    }
                }
        }
    }

    override fun processIntent(intent: EmptyIntent) = Unit
}
