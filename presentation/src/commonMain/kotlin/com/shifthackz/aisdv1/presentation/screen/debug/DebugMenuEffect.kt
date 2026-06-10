package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * Defines the `DebugMenuEffect` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DebugMenuEffect : MviEffect {
    /**
     * Carries `Message` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Message(val message: String) : DebugMenuEffect
}
