package com.shifthackz.aisdv1.presentation.screen.splash

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

interface SplashLoaderEffect : MviEffect {
    object ProceedNavigation : SplashLoaderEffect
}

interface SplashLoaderState : MviState {
    data class StatusNotification(val statusNotification: UiText) : SplashLoaderState
}
