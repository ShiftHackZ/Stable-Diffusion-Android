package com.shifthackz.aisdv1.presentation.screen.donate

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `DonateIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DonateIntent : MviIntent {

    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : DonateIntent

    /**
     * Provides the `LaunchDonate` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object LaunchDonate : DonateIntent
}
