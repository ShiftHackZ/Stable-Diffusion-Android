package com.shifthackz.aisdv1.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Renders the `RootBackHandler` UI for the SDAI presentation layer.
 *
 * @param enabled enabled value consumed by the API.
 * @param onBack callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun RootBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
