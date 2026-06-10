package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `DebugMenuIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DebugMenuIntent : MviIntent {

    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : DebugMenuIntent

    /**
     * Provides the `ViewLogs` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ViewLogs : DebugMenuIntent

    /**
     * Provides the `ClearLogs` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ClearLogs : DebugMenuIntent

    /**
     * Provides the `AllowLocalDiffusionCancel` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object AllowLocalDiffusionCancel : DebugMenuIntent

    /**
     * Provides the `InsertBadBase64` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object InsertBadBase64 : DebugMenuIntent

    /**
     * Defines the `LocalDiffusionScheduler` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface LocalDiffusionScheduler : DebugMenuIntent {

        /**
         * Carries `Confirm` data through the SDAI presentation layer.
         *
         * @param token token value consumed by the API.
         * @author Dmitriy Moroz
         */
        data class Confirm(val token: SchedulersToken) : DebugMenuIntent

        /**
         * Provides the `Request` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object Request : DebugMenuIntent
    }

    /**
     * Coordinates `WorkManager` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class WorkManager : DebugMenuIntent {
        CancelAll, RestartTxt2Img, RestartImg2Img;
    }

    /**
     * Provides the `DismissModal` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissModal : DebugMenuIntent
}
