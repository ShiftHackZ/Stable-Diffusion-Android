package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * Defines the `ServerSetupEffect` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ServerSetupEffect : MviEffect {
    /**
     * Provides the `HideKeyboard` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object HideKeyboard : ServerSetupEffect
    /**
     * Provides the `LaunchManageStoragePermission` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object LaunchManageStoragePermission : ServerSetupEffect
    /**
     * Carries `OpenUrl` data through the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    data class OpenUrl(val url: String) : ServerSetupEffect
}
