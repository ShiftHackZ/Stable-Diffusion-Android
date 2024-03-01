package com.shifthackz.aisdv1.presentation.widget.connectivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.MviComposable
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.theme.colors
import com.shifthackz.catppuccin.palette.Catppuccin
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConnectivityComposable() {
    MviComposable(
        viewModel = koinViewModel<ConnectivityViewModel>(),
        applySystemUiColors = false,
    ) { state, _ ->
        ConnectivityWidgetState(
            modifier = Modifier.fillMaxWidth(),
            state = state,
        )
    }
}

@Composable
private fun ConnectivityWidgetState(
    modifier: Modifier = Modifier,
    state: ConnectivityState,
) {
    if (!state.enabled) return
    val uiColor = when (state) {
        is ConnectivityState.Connected -> colors(light = Catppuccin.Latte.Green, dark = Catppuccin.Frappe.Green)
        is ConnectivityState.Disconnected -> colors(light = Catppuccin.Latte.Red, dark = Catppuccin.Frappe.Red)
        is ConnectivityState.Uninitialized -> colors(light = Catppuccin.Latte.Lavender, dark = Catppuccin.Frappe.Lavender)
    }
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .background(uiColor, shape = RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp, horizontal = 16.dp),
            text = stringResource(
                id = when (state) {
                    is ConnectivityState.Connected -> R.string.status_connected
                    is ConnectivityState.Disconnected -> R.string.status_disconnected
                    is ConnectivityState.Uninitialized -> R.string.status_communicating
                }
            ),
            color = colors(light = Catppuccin.Latte.Base, dark = Catppuccin.Frappe.Base)
        )
    }
}

@Composable
@Preview
private fun PreviewConnectivityComposableConnected() {
    ConnectivityWidgetState(
        Modifier.fillMaxWidth(),
        ConnectivityState.Connected(true),
    )
}

@Composable
@Preview
private fun PreviewConnectivityComposableDisconnected() {
    ConnectivityWidgetState(
        Modifier.fillMaxWidth(),
        ConnectivityState.Disconnected(true),
    )
}

@Composable
@Preview
private fun PreviewConnectivityComposableUninitialized() {
    ConnectivityWidgetState(
        Modifier.fillMaxWidth(),
        ConnectivityState.Uninitialized(true),
    )
}
