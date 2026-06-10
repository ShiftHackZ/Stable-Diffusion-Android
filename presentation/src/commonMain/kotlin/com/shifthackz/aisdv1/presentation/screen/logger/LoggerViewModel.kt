package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import kotlinx.coroutines.withContext

class LoggerViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val logReader: LogReader,
    private val router: LoggerRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<LoggerState, LoggerIntent, EmptyEffect>(
    initialState = LoggerState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        readLogs()
    }

    override fun processIntent(intent: LoggerIntent) {
        when (intent) {
            LoggerIntent.ReadLogs -> readLogs()
            LoggerIntent.NavigateBack -> router.navigateBack()
        }
    }

    private fun readLogs() {
        updateState { it.copy(loading = true, text = "") }
        launch(dispatchersProvider.io) {
            runCatching { logReader.read() }
                .onSuccess { content ->
                    withContext(dispatchersProvider.immediate) {
                        updateState {
                            it.copy(
                                loading = false,
                                text = content,
                            )
                        }
                    }
                }
                .onFailure { t ->
                    withContext(dispatchersProvider.immediate) {
                        updateState { it.copy(loading = false, text = "") }
                    }
                    onError(t)
                }
        }
    }
}
