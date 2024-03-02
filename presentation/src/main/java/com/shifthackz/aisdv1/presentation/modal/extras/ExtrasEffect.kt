package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.android.core.mvi.MviEffect

sealed interface ExtrasEffect : MviEffect {

    data class ApplyPrompts(
        val prompt: String,
        val negativePrompt: String,
    ) : ExtrasEffect

    data object Close : ExtrasEffect
}
