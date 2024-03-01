package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.ui.MviEffect

sealed interface ServerSetupEffect : MviEffect {

    data object CompleteSetup : ServerSetupEffect

    data object NavigateBack : ServerSetupEffect

    data object LaunchManageStoragePermission : ServerSetupEffect

    data class LaunchUrl(val url: String) : ServerSetupEffect
}
