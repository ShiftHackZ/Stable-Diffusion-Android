package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable

/**
 * Returns the top inset used only by the artificial status-bar color spacer above app bars.
 *
 * This app paints a spacer before the actual screen toolbar so the status-bar area gets the same
 * color as the top app bar. That spacer must follow the host window contract, not blindly apply
 * `WindowInsets.statusBars` everywhere. On legacy Android windows the system already lays the app out
 * below the status bar, so adding `statusBars` again pushes every top bar down by one status-bar
 * height. On enforced edge-to-edge Android and on iOS, the app is responsible for reserving that area.
 * Keeping this as a shared expect function makes the workaround visible at the boundary where the
 * platform behavior diverges.
 */
@Composable
internal expect fun persistentTopAppBarWindowInsets(): WindowInsets
