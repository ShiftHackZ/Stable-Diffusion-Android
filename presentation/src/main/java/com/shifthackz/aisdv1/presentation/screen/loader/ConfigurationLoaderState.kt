package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviState

interface ConfigurationLoaderState : MviState {
    data class StatusNotification(val statusNotification: UiText) : ConfigurationLoaderState
}
