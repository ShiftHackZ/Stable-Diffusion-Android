@file:Suppress("unused")

package com.shifthackz.aisdv1.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel

@Composable
fun <S : MviState, E : MviEffect> MviComposable(
    viewModel: MviViewModel<S, E>,
    effectHandler: (E) -> Unit = {},
    applySystemUiColors: Boolean = true,
    statusBarColor: Color = Color.Transparent,
    statusBarDarkIcons: Boolean = false,
    navigationBarColor: Color = Color.White,
    content: @Composable (S) -> Unit,
) = object : MviScreen<S, E>(viewModel) {

    @Composable
    override fun statusBarColor(): Color = statusBarColor

    @Composable
    override fun statusBarDarkIcons(): Boolean = statusBarDarkIcons

    @Composable
    override fun navigationBarColor(): Color = navigationBarColor

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsState().value
        content(state)
    }

    override fun processEffect(effect: E) {
        effectHandler(effect)
    }

    @Composable
    override fun ApplySystemUiColors() {
        if (applySystemUiColors) super.ApplySystemUiColors()
    }

}.Build()
