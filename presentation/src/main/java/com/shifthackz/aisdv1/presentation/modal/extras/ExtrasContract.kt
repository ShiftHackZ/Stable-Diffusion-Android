package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.presentation.model.ExtraType

data class ExtrasState(
    val loading: Boolean = true,
    val prompt: String = "",
    val negativePrompt: String = "",
    val type: ExtraType = ExtraType.Lora,
    val loras: List<ExtraItemUi> = emptyList(),
) : MviState

sealed interface ExtrasEffect : MviEffect {
    data class ApplyPrompts(
        val prompt: String,
        val negativePrompt: String,
    ) : ExtrasEffect
}

data class ExtraItemUi(
    val type: ExtraType,
    val key: String,
    val name: String,
    val alias: String?,
    val isApplied: Boolean,
    val value: String? = null,
)
