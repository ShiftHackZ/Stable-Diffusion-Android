@file:Suppress("unused")

package com.shifthackz.aisdv1.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel2

@Composable
fun <S : MviState, I: MviIntent, E : MviEffect> MviComposable2(
    viewModel: MviViewModel2<S, I, E>,
    effectHandler: (E) -> Unit = {},
    statusBarColor: Color = MaterialTheme.colorScheme.background,
    statusBarDarkIcons: Boolean = statusBarColor.luminance() > 0.5f,
    navigationBarColor: Color =  MaterialTheme.colorScheme.background,
    content: @Composable (S) -> Unit,
) = object : MviScreen2<S, I, E>(viewModel) {

    @Composable
    override fun statusBarColor(): Color = statusBarColor

    @Composable
    override fun statusBarDarkIcons(): Boolean = statusBarDarkIcons

    @Composable
    override fun navigationBarColor(): Color = navigationBarColor

    @Composable
    override fun Content() {
        val state = viewModel.state.collectAsStateWithLifecycle().value
        content(state)
    }

    override fun processEffect(effect: E) {
        effectHandler(effect)
    }
}.Build()


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
        val state = viewModel.state.collectAsStateWithLifecycle().value
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
