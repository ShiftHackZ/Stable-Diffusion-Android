package com.shifthackz.aisdv1.presentation.modal.tag

import com.shifthackz.android.core.mvi.MviEffect

sealed interface EditTagEffect : MviEffect {

    data object Close : EditTagEffect

    data class ApplyPrompts(
        val prompt: String,
        val negativePrompt: String,
    ) : EditTagEffect
}
