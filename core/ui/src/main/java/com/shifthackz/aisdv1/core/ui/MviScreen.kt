@file:Suppress("MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel

abstract class MviScreen<S: MviState, E: MviEffect>(
    private val viewModel: MviViewModel<S, E>,
) {
    protected open val statusBarColor
        get() = Color.Transparent

    protected open val statusBarDarkIcons
        get() = statusBarColor.luminance() > 0.5f

    protected open val navigationBarColor
        get() = Color.White

    @Composable
    fun Build() {
        LaunchedEffect(KEY_EFFECTS_PROCESSOR) {
            viewModel.effectStream.collect(::processEffect)
        }
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

    protected open fun processEffect(effect: E) = Unit

    companion object {
        private const val KEY_EFFECTS_PROCESSOR = "key_effects_processor"
    }
}
