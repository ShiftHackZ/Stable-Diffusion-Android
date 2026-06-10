package com.shifthackz.aisdv1.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Renders the `MviComponent` UI for the SDAI presentation layer.
 *
 * @param viewModel view model value consumed by the API.
 * @param processEffect process effect value consumed by the API.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun <S : MviState, I : MviIntent, E : MviEffect> MviComponent(
    viewModel: MviViewModel<S, I, E>,
    processEffect: (effect: E) -> Unit = {},
    content: @Composable (state: S) -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect(processEffect)
    }
    val state by viewModel.state.collectAsState()
    content(state)
}

/**
 * Renders the `MviComponent` UI for the SDAI presentation layer.
 *
 * @param viewModel view model value consumed by the API.
 * @param processEffect process effect value consumed by the API.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun <S : MviState, I : MviIntent, E : MviEffect> MviComponent(
    viewModel: MviViewModel<S, I, E>,
    processEffect: (effect: E) -> Unit = {},
    content: @Composable (state: S, processIntent: (I) -> Unit) -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collect(processEffect)
    }
    val state by viewModel.state.collectAsState()
    content(state, viewModel::processIntent)
}
