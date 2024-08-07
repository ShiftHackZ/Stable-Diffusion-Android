package com.shifthackz.aisdv1.presentation.widget.work

import com.shifthackz.android.core.mvi.MviIntent

sealed interface BackgroundWorkIntent : MviIntent {
    data object Dismiss : BackgroundWorkIntent
}
