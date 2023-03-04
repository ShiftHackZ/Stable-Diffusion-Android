package com.shifthackz.aisdv1.presentation.screen.splash.contract

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviState

interface SplashLoaderState : MviState {
    data class StatusNotification(val statusNotification: UiText) : SplashLoaderState
}
