package com.shifthackz.aisdv1.presentation.screen.history

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface HistoryIntent : MviIntent {
    data object NavigateBack : HistoryIntent
    data object Refresh : HistoryIntent
}
