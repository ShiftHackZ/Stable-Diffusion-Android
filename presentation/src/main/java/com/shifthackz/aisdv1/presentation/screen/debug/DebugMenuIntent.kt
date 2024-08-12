package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.android.core.mvi.MviIntent

sealed interface DebugMenuIntent : MviIntent {

    data object NavigateBack : DebugMenuIntent

    data object ViewLogs : DebugMenuIntent

    data object ClearLogs : DebugMenuIntent

    data object AllowLocalDiffusionCancel : DebugMenuIntent

    data object InsertBadBase64 : DebugMenuIntent

    sealed interface LocalDiffusionScheduler : DebugMenuIntent {

        data class Confirm(val token: SchedulersToken) : DebugMenuIntent

        data object Request : DebugMenuIntent
    }

    enum class WorkManager : DebugMenuIntent {
        CancelAll, RestartTxt2Img, RestartImg2Img;
    }

    data object DismissModal : DebugMenuIntent
}
