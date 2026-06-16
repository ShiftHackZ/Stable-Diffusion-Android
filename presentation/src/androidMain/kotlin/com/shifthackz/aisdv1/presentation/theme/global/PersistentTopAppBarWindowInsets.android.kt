package com.shifthackz.aisdv1.presentation.theme.global

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable

private const val EDGE_TO_EDGE_ENFORCED_API = 35

/**
 * Android implementation for the status-bar color spacer used by top app bars.
 *
 * On Android API 35 and newer, the target-SDK edge-to-edge rules mean the content can extend into the
 * status-bar region, so the spacer must consume `WindowInsets.statusBars` to paint and reserve that
 * area. On older Android releases, the decor view commonly provides a content window that already
 * starts below the status bar; applying `statusBars` there creates a duplicated top inset. The Pixel
 * 3a XL/API 32 regression showed this as every screen toolbar being shifted down by exactly the status
 * bar height. Returning zero before API 35 keeps legacy devices on their resized-window contract while
 * preserving the required edge-to-edge spacing on newer devices.
 */
@Composable
internal actual fun persistentTopAppBarWindowInsets(): WindowInsets =
    if (Build.VERSION.SDK_INT >= EDGE_TO_EDGE_ENFORCED_API) {
        WindowInsets.statusBars
    } else {
        WindowInsets(0, 0, 0, 0)
    }
