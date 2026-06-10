package com.shifthackz.aisdv1.presentation.screen.logger

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `LoggerIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface LoggerIntent : MviIntent {
    /**
     * Provides the `ReadLogs` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ReadLogs : LoggerIntent
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : LoggerIntent
}
