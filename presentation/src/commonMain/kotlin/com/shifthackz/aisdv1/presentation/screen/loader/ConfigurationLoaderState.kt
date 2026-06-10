package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `ConfigurationLoaderState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class ConfigurationLoaderState(
    /**
     * Exposes the `status` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val status: Status = Status.Initializing,
) : MviState {
    enum class Status {
        Initializing,
        Fetching,
        Failed,
        Launching,
    }
}
