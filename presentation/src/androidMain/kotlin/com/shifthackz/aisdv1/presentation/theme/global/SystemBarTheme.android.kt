package com.shifthackz.aisdv1.presentation.theme.global

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Renders the `ApplySystemBarTheme` UI for the SDAI presentation layer.
 *
 * @param colorScheme color scheme value consumed by the API.
 * @param isDark is dark value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun ApplySystemBarTheme(
    colorScheme: ColorScheme,
    isDark: Boolean,
) {
    val view = LocalView.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    if (view.isInEditMode) return
    fun apply() {
        val dialogWindow = (view.parent as? DialogWindowProvider)?.window
        dialogWindow?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val windows = listOfNotNull(
            context.findActivity()?.window,
            view.context.findActivity()?.window,
            dialogWindow,
        ).distinct()
        val barColor = colorScheme.surface.toArgb()
        windows.forEach { window ->
            window.applySystemBars(
                color = barColor,
                lightBars = !isDark,
            )
        }
    }
    SideEffect {
        apply()
    }
    DisposableEffect(lifecycleOwner, view, colorScheme, isDark) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                apply()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Executes the `applySystemBars` step in the SDAI presentation layer.
 *
 * @param color color value consumed by the API.
 * @param lightBars light bars value consumed by the API.
 * @author Dmitriy Moroz
 */
private fun Window.applySystemBars(
    color: Int,
    lightBars: Boolean,
) {
    fun applyColors() {
        setBackgroundDrawable(ColorDrawable(color))
        decorView.setBackgroundColor(color)
        decorView.rootView.setBackgroundColor(color)
        decorView.findViewById<View?>(android.R.id.statusBarBackground)?.setBackgroundColor(color)
        decorView.findViewById<View?>(android.R.id.navigationBarBackground)?.setBackgroundColor(color)
        statusBarColor = color
        navigationBarColor = color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isStatusBarContrastEnforced = false
            isNavigationBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(this, decorView).run {
            isAppearanceLightStatusBars = lightBars
            isAppearanceLightNavigationBars = lightBars
        }
        applySystemBarIconMode(lightBars)
    }
    applyColors()
    decorView.post { applyColors() }
}

/**
 * Executes the `applySystemBarIconMode` step in the SDAI presentation layer.
 *
 * @param lightBars light bars value consumed by the API.
 * @author Dmitriy Moroz
 */
@Suppress("DEPRECATION")
private fun Window.applySystemBarIconMode(lightBars: Boolean) {
    var flags = decorView.systemUiVisibility
    flags = if (lightBars) {
        flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = if (lightBars) {
            flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
    decorView.systemUiVisibility = flags

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        val appearance = if (lightBars) mask else 0
        insetsController?.setSystemBarsAppearance(appearance, mask)
    }
}

/**
 * Executes the `findActivity` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
