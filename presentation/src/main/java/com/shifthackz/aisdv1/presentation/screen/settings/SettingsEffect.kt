package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.android.core.mvi.MviEffect

sealed interface SettingsEffect : MviEffect {

    sealed interface RequestPermission : SettingsEffect {
        data object Storage : SettingsEffect
        data object Notifications : SettingsEffect
    }

    data object ShareLogFile : SettingsEffect

    data object DeveloperModeUnlocked : SettingsEffect

    data class OpenUrl(val url: String) : SettingsEffect
}
