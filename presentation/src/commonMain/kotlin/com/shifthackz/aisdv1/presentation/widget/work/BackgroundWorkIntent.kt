package com.shifthackz.aisdv1.presentation.widget.work

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `BackgroundWorkIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface BackgroundWorkIntent : MviIntent {
    /**
     * Provides the `Dismiss` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Dismiss : BackgroundWorkIntent
}
