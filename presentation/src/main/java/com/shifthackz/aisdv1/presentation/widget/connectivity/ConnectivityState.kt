package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.android.core.mvi.MviState

sealed interface ConnectivityState : MviState {

    val enabled: Boolean

    data class Uninitialized(override val enabled: Boolean = true) : ConnectivityState
    data class Connected(override val enabled: Boolean = true) : ConnectivityState
    data class Disconnected(override val enabled: Boolean = true) : ConnectivityState

    companion object {
        fun consume(value: Pair<Boolean, Boolean>): ConnectivityState {
            val (connected, enabled) = value
            return if (connected) Connected(enabled) else Disconnected(enabled)
        }
    }
}
