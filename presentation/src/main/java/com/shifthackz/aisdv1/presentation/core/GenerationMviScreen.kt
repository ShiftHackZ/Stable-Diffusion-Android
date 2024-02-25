package com.shifthackz.aisdv1.presentation.core

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel

abstract class GenerationMviScreen<S: MviState, E: GenerationMviEffect>(
    viewModel: MviViewModel<S, E>,
    private val launchGalleryDetail: (Long) -> Unit,
) : MviScreen<S, E>(viewModel) {

    override fun processEffect(effect: E) = when (effect) {
        is GenerationMviEffect.LaunchGalleryDetail -> launchGalleryDetail(effect.itemId)
        else -> super.processEffect(effect)
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}
