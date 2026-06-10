package com.shifthackz.aisdv1.presentation.screen.logger

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `LoggerState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class LoggerState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `text` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val text: String = "",
) : MviState
