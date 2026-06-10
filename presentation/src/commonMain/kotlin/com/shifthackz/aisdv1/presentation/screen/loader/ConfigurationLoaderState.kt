package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.mvi.MviState

data class ConfigurationLoaderState(
    val status: Status = Status.Initializing,
) : MviState {
    enum class Status {
        Initializing,
        Fetching,
        Failed,
        Launching,
    }
}
