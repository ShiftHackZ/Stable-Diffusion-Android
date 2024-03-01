//@file:Suppress("unused")
//
//package com.shifthackz.aisdv1.core.ui
//
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.luminance
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.shifthackz.aisdv1.core.viewmodel.MviViewModel
//
//@Composable
//fun <S : MviState, I: MviIntent, E : MviEffect> MviComponent(
//    viewModel: MviViewModel<S, I, E>,
//    effectHandler: (effect: E) -> Unit = {},
//    applySystemUiColors: Boolean = true,
//    statusBarColor: Color = MaterialTheme.colorScheme.background,
//    statusBarDarkIcons: Boolean = statusBarColor.luminance() > 0.5f,
//    navigationBarColor: Color =  MaterialTheme.colorScheme.background,
//    content: @Composable (state: S, intentHandler: (I) -> Unit) -> Unit,
//) = object : MviScreen<S, I, E>(viewModel) {
//
//    @Composable
//    override fun statusBarColor(): Color = statusBarColor
//
//    @Composable
//    override fun statusBarDarkIcons(): Boolean = statusBarDarkIcons
//
//    @Composable
//    override fun navigationBarColor(): Color = navigationBarColor
//
//    @Composable
//    override fun Content() {
//        val state = viewModel.state.collectAsStateWithLifecycle().value
//        content(state, viewModel::processIntent)
//    }
//
//    @Composable
//    override fun Build() {
//        LaunchedEffect(KEY_EFFECTS_PROCESSOR) {
//            viewModel.effectStream.collect(::processEffect)
//        }
//        if (applySystemUiColors) {
//            ApplySystemUiColors()
//        }
//        Content()
//    }
//
//    override fun processEffect(effect: E) {
//        effectHandler(effect)
//    }
//}.Build()
