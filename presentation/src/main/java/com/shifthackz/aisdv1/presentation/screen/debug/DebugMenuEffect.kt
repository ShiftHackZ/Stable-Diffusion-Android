package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.android.core.mvi.MviEffect

sealed interface DebugMenuEffect : MviEffect {
    data class Message(val message: UiText) : DebugMenuEffect
}
