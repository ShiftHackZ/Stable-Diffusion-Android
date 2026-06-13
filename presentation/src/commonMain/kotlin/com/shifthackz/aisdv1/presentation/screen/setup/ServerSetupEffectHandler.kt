package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.presentation.platform.ExternalUrlLauncher

/**
 * Renders the `rememberServerSetupEffectHandler` UI for the SDAI presentation layer.
 *
 * @param urlLauncher URL launcher consumed by the handler.
 * @return Result produced by `rememberServerSetupEffectHandler`.
 * @author Dmitriy Moroz
 */
@Composable
internal expect fun rememberServerSetupEffectHandler(
    urlLauncher: ExternalUrlLauncher,
): (ServerSetupEffect) -> Unit
