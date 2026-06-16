package com.shifthackz.aisdv1.presentation.theme.global

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable

private const val EDGE_TO_EDGE_ENFORCED_API = 35

/**
 * Android implementation for bottom bars that remain pinned to the screen edge.
 *
 * Android 15/API 35 started enforcing edge-to-edge for apps targeting the new SDK, so on those
 * devices the app can draw behind the navigation area and must explicitly keep persistent controls
 * clear of `WindowInsets.navigationBars`. Older Android releases, especially the Pixel 3a XL/API 32
 * case with the classic three-button navigation bar, already give the app a window whose bottom is
 * above the system bar. Applying `navigationBars` there adds the same inset a second time, producing
 * the release-only-looking gap that triggered this helper. Returning zero before API 35 preserves the
 * legacy window contract; returning `navigationBars` on API 35+ preserves enforced edge-to-edge.
 */
@Composable
internal actual fun persistentBottomBarWindowInsets(): WindowInsets =
    if (Build.VERSION.SDK_INT >= EDGE_TO_EDGE_ENFORCED_API) {
        WindowInsets.navigationBars
    } else {
        WindowInsets(0, 0, 0, 0)
    }
