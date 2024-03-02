package com.shifthackz.aisdv1.presentation.screen.loader

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.android.core.mvi.MviState

interface ConfigurationLoaderState : MviState {

    @Immutable
    data class StatusNotification(val statusNotification: UiText) : ConfigurationLoaderState
}
