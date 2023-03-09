package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.ui.MviState

interface SettingsState : MviState {
    object Uninitialized : SettingsState

    data class Content(
        val sdModels: List<String>,
        val sdModelSelected: String,
    ) : SettingsState
}
