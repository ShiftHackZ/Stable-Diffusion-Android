package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.android.core.mvi.MviState

data class DebugMenuState(
    val allowLocalDiffusionCancel: Boolean = false,
) : MviState
