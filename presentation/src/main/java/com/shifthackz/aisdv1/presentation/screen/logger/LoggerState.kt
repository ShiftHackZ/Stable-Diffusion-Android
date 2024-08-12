package com.shifthackz.aisdv1.presentation.screen.logger

import androidx.compose.runtime.Immutable
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class LoggerState(
    val loading: Boolean = true,
    val text: String = "",
) : MviState
