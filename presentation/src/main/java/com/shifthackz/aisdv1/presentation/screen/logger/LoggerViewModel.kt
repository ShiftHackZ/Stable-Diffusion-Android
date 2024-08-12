package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import java.io.File

class LoggerViewModel(
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val mainRouter: MainRouter,
) : MviRxViewModel<LoggerState, LoggerIntent, EmptyEffect>() {

    override val initialState = LoggerState()

    init {
        readLogs()
    }

    override fun processIntent(intent: LoggerIntent) {
        when (intent) {
            LoggerIntent.ReadLogs -> readLogs()
            LoggerIntent.NavigateBack -> mainRouter.navigateBack()
        }
    }

    private fun readLogs() {
        updateState { it.copy(loading = true, text = "") }
        try {
            val logFile = File(
                fileProviderDescriptor.logsCacheDirPath +
                        "/" +
                        FileLoggingTree.LOGGER_FILENAME
            )
            val content = logFile.readText()
            updateState {
                it.copy(
                    loading = false,
                    text = content,
                )
            }
        } catch (e: Exception) {
            updateState { it.copy(loading = false) }
        }
    }
}
