package com.shifthackz.aisdv1.presentation.screen.settings

import com.shifthackz.aisdv1.core.ui.MviEffect

sealed interface SettingsEffect : MviEffect {

    data object RequestStoragePermission : SettingsEffect

    data object ShareLogFile : SettingsEffect

    data class OpenUrl(val url: String) : SettingsEffect
}
