@file:Suppress("MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel2

abstract class MviScreen2<S : MviState, I : MviIntent, E : MviEffect>(
    private val viewModel: MviViewModel2<S, I, E>,
) : Screen() {

    @Composable
    override fun Build() {
        LaunchedEffect(KEY_EFFECTS_PROCESSOR) {
            viewModel.effectStream.collect(::processEffect)
        }
        super.Build()
    }

    protected open fun processEffect(effect: E) = Unit

    companion object {
        private const val KEY_EFFECTS_PROCESSOR = "key_effects_processor"
    }
}
