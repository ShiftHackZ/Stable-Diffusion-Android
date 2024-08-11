package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.android.core.mvi.MviIntent

enum class DebugMenuIntent : MviIntent {
    NavigateBack,
    ViewLogs,
    ClearLogs,
    InsertBadBase64;
}
