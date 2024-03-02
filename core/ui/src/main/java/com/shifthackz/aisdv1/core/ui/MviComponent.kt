package com.shifthackz.aisdv1.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shifthackz.android.core.mvi.MviComponent
import com.shifthackz.android.core.mvi.MviEffect
import com.shifthackz.android.core.mvi.MviIntent
import com.shifthackz.android.core.mvi.MviState
import com.shifthackz.android.core.mvi.MviViewModel

@Composable
fun <S : MviState, I: MviIntent, E : MviEffect> MviComponent(
    viewModel: MviViewModel<S, I, E>,
    processEffect: (effect: E) -> Unit = {},
    applySystemUiColors: Boolean = true,
    navigationBarColor: Color =  MaterialTheme.colorScheme.background,
    content: @Composable (state: S, intentHandler: (I) -> Unit) -> Unit,
) {
    if (applySystemUiColors) {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setNavigationBarColor(navigationBarColor)
        }
    }
    MviComponent(
        viewModel = viewModel,
        processEffect = processEffect,
        content = content,
    )
}
