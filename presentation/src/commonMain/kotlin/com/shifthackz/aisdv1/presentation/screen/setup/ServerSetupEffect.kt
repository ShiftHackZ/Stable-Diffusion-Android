package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.mvi.MviEffect

sealed interface ServerSetupEffect : MviEffect {
    data object HideKeyboard : ServerSetupEffect
    data object LaunchManageStoragePermission : ServerSetupEffect
    data class OpenUrl(val url: String) : ServerSetupEffect
}
