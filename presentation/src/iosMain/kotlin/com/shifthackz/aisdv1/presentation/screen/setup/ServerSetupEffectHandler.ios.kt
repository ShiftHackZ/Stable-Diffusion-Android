package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.shifthackz.aisdv1.presentation.platform.ExternalUrlLauncher

/**
 * Renders the `rememberServerSetupEffectHandler` UI for the SDAI presentation layer.
 *
 * @param urlLauncher URL launcher consumed by the handler.
 * @return Result produced by `rememberServerSetupEffectHandler`.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun rememberServerSetupEffectHandler(
    urlLauncher: ExternalUrlLauncher,
): (ServerSetupEffect) -> Unit {
    val keyboardController = LocalSoftwareKeyboardController.current
    return remember(keyboardController, urlLauncher) {
        { effect ->
            when (effect) {
                ServerSetupEffect.HideKeyboard -> keyboardController?.hide()
                ServerSetupEffect.LaunchManageStoragePermission -> Unit
                is ServerSetupEffect.OpenUrl -> urlLauncher.openUrl(effect.url)
            }
        }
    }
}
