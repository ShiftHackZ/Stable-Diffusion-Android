package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface LoggerIntent : MviIntent {
    data object ReadLogs : LoggerIntent
    data object NavigateBack : LoggerIntent
}
