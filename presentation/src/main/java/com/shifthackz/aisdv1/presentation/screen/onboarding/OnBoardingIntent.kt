package com.shifthackz.aisdv1.presentation.screen.onboarding

import com.shifthackz.android.core.mvi.MviIntent

sealed interface OnBoardingIntent : MviIntent {
    data object Navigate : OnBoardingIntent
}
