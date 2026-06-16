package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable

/**
 * Returns the bottom system inset that persistent bottom controls should apply.
 *
 * This exists as a named API because using `WindowInsets.navigationBars` directly at every call site
 * is wrong for this app: some hosts already resize the content window above the navigation bar, while
 * newer edge-to-edge hosts expect the app to reserve that space itself. The visible failure was a
 * Docker/Play release looking different from an Android Studio install on a Pixel 3a XL with the
 * three-button navigation bar: bottom navigation and sticky buttons were lifted by exactly one
 * navigation-bar height. Callers should use this helper for persistent bottom UI instead of deciding
 * platform inset behavior locally.
 */
@Composable
internal expect fun persistentBottomBarWindowInsets(): WindowInsets
