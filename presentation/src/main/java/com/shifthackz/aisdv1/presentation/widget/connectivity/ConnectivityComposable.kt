package com.shifthackz.aisdv1.presentation.widget.connectivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.measureTextWidth
import com.shifthackz.aisdv1.presentation.theme.colors
import com.shifthackz.android.core.mvi.MviComponent
import com.shifthackz.catppuccin.palette.Catppuccin
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ConnectivityComposable() {
    MviComponent(
        viewModel = koinViewModel<ConnectivityViewModel>(),
    ) { state, _ ->
        ConnectivityWidgetState(
            state = state,
        )
    }
}

@Composable
private fun ConnectivityWidgetState(
    modifier: Modifier = Modifier,
    state: ConnectivityState,
) {
    val text = stringResource(
        id = when (state) {
            is ConnectivityState.Connected -> LocalizationR.string.status_connected
            is ConnectivityState.Disconnected -> LocalizationR.string.status_disconnected
            is ConnectivityState.Uninitialized -> LocalizationR.string.status_communicating
        }
    )
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val targetOffset = with(LocalDensity.current) {
        measureTextWidth(text = text).toPx()
    }

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = targetOffset,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset",
    )
    AnimatedVisibility(visible = state.enabled) {
        val uiColor = when (state) {
            is ConnectivityState.Connected -> colors(
                light = Catppuccin.Latte.Green,
                dark = Catppuccin.Frappe.Green
            )

            is ConnectivityState.Disconnected -> colors(
                light = Catppuccin.Latte.Red,
                dark = Catppuccin.Frappe.Red
            )

            is ConnectivityState.Uninitialized -> colors(
                light = Catppuccin.Latte.Lavender,
                dark = Catppuccin.Frappe.Lavender
            )
        }
        Column(
            modifier = modifier
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .drawWithCache {
                        val brushSize = 40f
                        val brushAnimated = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.5f),
                                Color.Transparent,
                            ),
                            start = Offset(offset, offset),
                            end = Offset(offset + brushSize, offset + brushSize),
                            tileMode = TileMode.Clamp,
                        )
                        onDrawBehind {
                            drawRoundRect(
                                color = uiColor,
                                cornerRadius = CornerRadius(8.dp.toPx()),
                            )
                            if (state !is ConnectivityState.Uninitialized) return@onDrawBehind
                            drawRoundRect(
                                cornerRadius = CornerRadius(8.dp.toPx()),
                                brush = brushAnimated,
                            )
                        }
                    }
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                text = text,
                color = colors(light = Catppuccin.Latte.Base, dark = Catppuccin.Frappe.Base)
            )
        }
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
