package com.shifthackz.aisdv1.presentation.app

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController

/**
 * Executes the `MainViewController` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
fun MainViewController() = ComposeUIViewController(
    configure = {
        onFocusBehavior = OnFocusBehavior.DoNothing
    },
) {
    AiSdApp()
}
