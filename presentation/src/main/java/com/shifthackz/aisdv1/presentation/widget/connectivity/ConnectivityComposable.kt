package com.shifthackz.aisdv1.presentation.widget.connectivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R

class ConnectivityComposable(
    private val viewModel: ConnectivityViewModel,
) : MviScreen<ConnectivityState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        ConnectivityWidgetState(
            modifier = Modifier.fillMaxWidth(),
            state = viewModel.state.collectAsState().value,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ConnectivityWidgetState(
    modifier: Modifier = Modifier,
    state: ConnectivityState,
) {
    if (!state.enabled) return
    val uiColor = when (state) {
        is ConnectivityState.Connected -> MaterialTheme.colorScheme.primaryContainer
        is ConnectivityState.Disconnected -> MaterialTheme.colorScheme.errorContainer
        is ConnectivityState.Uninitialized -> MaterialTheme.colorScheme.secondaryContainer
    }
    Column(
        modifier = modifier.padding(top = 4.dp),
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
            color = when (state) {
                is ConnectivityState.Connected -> MaterialTheme.colorScheme.onPrimaryContainer
                is ConnectivityState.Disconnected -> MaterialTheme.colorScheme.onErrorContainer
                is ConnectivityState.Uninitialized -> MaterialTheme.colorScheme.onSurface
            }
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
