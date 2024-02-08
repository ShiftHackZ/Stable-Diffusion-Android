package com.shifthackz.aisdv1.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController

abstract class Screen {

    @Composable
    protected open fun statusBarColor(): Color = MaterialTheme.colorScheme.background

    @Composable
    protected open fun statusBarDarkIcons(): Boolean = statusBarColor().luminance() > 0.5f

    @Composable
    protected open fun navigationBarColor(): Color = MaterialTheme.colorScheme.background

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
        val navigationBarColor = navigationBarColor()
        SideEffect {
            systemUiController.setNavigationBarColor(navigationBarColor)
        }
    }
}
