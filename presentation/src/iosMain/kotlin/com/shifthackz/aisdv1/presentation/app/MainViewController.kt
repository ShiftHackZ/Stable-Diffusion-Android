package com.shifthackz.aisdv1.presentation.app

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = {
        onFocusBehavior = OnFocusBehavior.DoNothing
    },
) {
    AiSdApp()
}
