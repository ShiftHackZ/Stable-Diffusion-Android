package com.shifthackz.aisdv1.presentation.screen.history

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `HistoryIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface HistoryIntent : MviIntent {
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : HistoryIntent
    /**
     * Provides the `Refresh` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Refresh : HistoryIntent
}
