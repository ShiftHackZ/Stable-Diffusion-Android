package com.shifthackz.aisdv1.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        val lifecycleOwner = LocalLifecycleOwner.current
        val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

        LaunchedEffect(lifecycleState) {
            when (lifecycleState) {
                Lifecycle.State.INITIALIZED,
                Lifecycle.State.RESUMED,
                Lifecycle.State.CREATED -> {
                    systemUiController.setNavigationBarColor(navigationBarColor)
                }
                else -> Unit
            }
        }
    }
    MviComponent(
        viewModel = viewModel,
        processEffect = processEffect,
        content = content,
    )
}
