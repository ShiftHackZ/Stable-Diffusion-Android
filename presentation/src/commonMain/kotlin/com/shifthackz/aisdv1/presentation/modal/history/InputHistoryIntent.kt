package com.shifthackz.aisdv1.presentation.modal.history

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface InputHistoryIntent : MviIntent {

    data object LoadNextPage : InputHistoryIntent

    data object Retry : InputHistoryIntent
}
