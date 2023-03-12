package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.ui.MviState

sealed interface ConnectivityState : MviState {
    object Uninitialized : ConnectivityState
    object Connected : ConnectivityState
    object Disconnected : ConnectivityState

    companion object {
        fun consume(value: Boolean): ConnectivityState = if (value) Connected else Disconnected
    }
}
