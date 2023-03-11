package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

interface ConfigurationLoaderEffect : MviEffect {
    object ProceedNavigation : ConfigurationLoaderEffect
}

interface ConfigurationLoaderState : MviState {
    data class StatusNotification(val statusNotification: UiText) : ConfigurationLoaderState
}
