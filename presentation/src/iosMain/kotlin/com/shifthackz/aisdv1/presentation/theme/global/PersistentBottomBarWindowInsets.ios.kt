package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable

/**
 * iOS implementation for bottom bars that remain pinned to the screen edge.
 *
 * The Android workaround must not leak into iOS because the UIKit/Compose host keeps the usual safe
 * area contract for home indicator and bottom system affordances. Persistent buttons and navigation
 * bars still need `WindowInsets.navigationBars` here to avoid sitting under the home indicator. The
 * release regression was caused by Android's split behavior between legacy resized windows and
 * enforced edge-to-edge, not by a common Compose layout problem. Keeping the iOS actual explicit makes
 * the platform split intentional and protects future cleanups from flattening both platforms back to
 * the same, Android-biased inset rule.
 */
@Composable
internal actual fun persistentBottomBarWindowInsets(): WindowInsets = WindowInsets.navigationBars
