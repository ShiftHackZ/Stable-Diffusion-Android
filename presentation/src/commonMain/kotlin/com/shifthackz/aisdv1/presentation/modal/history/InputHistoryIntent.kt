package com.shifthackz.aisdv1.presentation.modal.history

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `InputHistoryIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface InputHistoryIntent : MviIntent {

    /**
     * Provides the `LoadNextPage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object LoadNextPage : InputHistoryIntent

    /**
     * Provides the `Retry` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Retry : InputHistoryIntent
}
