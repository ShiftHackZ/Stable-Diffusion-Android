package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Defines the `ErrorState` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ErrorState : MviState {

    /**
     * Provides the `None` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : ErrorState

    /**
     * Provides the `Generic` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Generic : ErrorState

    /**
     * Carries `WithMessage` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Immutable
    data class WithMessage(val message: UiText) : ErrorState
}
