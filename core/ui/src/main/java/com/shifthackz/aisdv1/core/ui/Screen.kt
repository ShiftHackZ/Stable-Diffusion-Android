package com.shifthackz.aisdv1.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController

abstract class Screen {

    protected open val statusBarColor
        get() = Color.Transparent

    protected open val statusBarDarkIcons
        get() = statusBarColor.luminance() > 0.5f

    protected open val navigationBarColor
        get() = Color.White

    @Composable
    open fun Build() {
        ApplySystemUiColors()
        Content()
    }

    @Composable
    protected abstract fun Content()

    @Composable
    protected open fun ApplySystemUiColors() {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(statusBarColor, statusBarDarkIcons)
            systemUiController.setNavigationBarColor(navigationBarColor)
        }
    }
}
