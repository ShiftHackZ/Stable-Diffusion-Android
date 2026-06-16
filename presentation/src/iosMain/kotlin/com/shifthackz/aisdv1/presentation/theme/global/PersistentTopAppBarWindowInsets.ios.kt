package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable

/**
 * iOS implementation for the status-bar color spacer used by top app bars.
 *
 * iOS keeps the expected safe-area behavior for the Compose host: the app bar background must extend
 * through the status-bar area, and the layout must reserve that safe area so titles and controls do
 * not collide with system indicators. The duplicated-top-inset bug was Android-specific and came from
 * mixing a legacy resized content window with explicit Compose status-bar padding. Returning
 * `WindowInsets.statusBars` here keeps the iOS behavior unchanged while documenting that the Android
 * zero-inset branch is a compatibility workaround, not a cross-platform design decision.
 */
@Composable
internal actual fun persistentTopAppBarWindowInsets(): WindowInsets = WindowInsets.statusBars
