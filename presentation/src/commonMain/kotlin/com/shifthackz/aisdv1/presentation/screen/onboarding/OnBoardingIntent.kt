package com.shifthackz.aisdv1.presentation.screen.onboarding

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `OnBoardingIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface OnBoardingIntent : MviIntent {
    /**
     * Provides the `Navigate` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Navigate : OnBoardingIntent
}
