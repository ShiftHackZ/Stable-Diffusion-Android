package com.shifthackz.aisdv1.presentation.screen.drawer

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `DrawerIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DrawerIntent : MviIntent {
    /**
     * Provides the `Open` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Open : DrawerIntent
    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : DrawerIntent
}
