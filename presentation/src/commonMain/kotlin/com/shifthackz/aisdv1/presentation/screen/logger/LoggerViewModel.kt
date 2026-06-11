package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import kotlinx.coroutines.withContext

/**
 * Coordinates `LoggerViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class LoggerViewModel(
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `logReader` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val logReader: LogReader,
    /**
     * Exposes the `platformActions` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val platformActions: LoggerPlatformActions,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: LoggerRouter,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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
            LoggerIntent.CopyLogs -> runLogAction(platformActions::copyLogs)
            LoggerIntent.ShareLogs -> runLogAction(platformActions::shareLogs)
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

    private fun runLogAction(action: suspend (String) -> Unit) {
        val text = currentState.text.takeIf(String::isNotBlank) ?: return
        launch(dispatchersProvider.immediate) {
            runCatching { action(text) }
                .onFailure(onError)
        }
    }
}
