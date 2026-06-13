package com.shifthackz.aisdv1.presentation.screen.setup.platform

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.presentation.platform.ExternalUrlLauncher
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupEffect

@Composable
internal expect fun rememberServerSetupEffectHandler(
    urlLauncher: ExternalUrlLauncher,
): (ServerSetupEffect) -> Unit
