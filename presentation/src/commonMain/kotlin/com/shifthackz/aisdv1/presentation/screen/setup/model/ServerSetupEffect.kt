package com.shifthackz.aisdv1.presentation.screen.setup.model

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * One-shot platform side effects emitted by the provider setup wizard.
 */
sealed interface ServerSetupEffect : MviEffect {
    data object HideKeyboard : ServerSetupEffect
    data object LaunchManageStoragePermission : ServerSetupEffect
    data class OpenUrl(val url: String) : ServerSetupEffect
}
