package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.mvi.MviEffect

sealed interface DebugMenuEffect : MviEffect {
    data class Message(val message: String) : DebugMenuEffect
}
