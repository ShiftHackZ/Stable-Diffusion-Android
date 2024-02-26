package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.presentation.model.ExtraType

sealed interface ExtrasState : MviState {

    data object Loading : ExtrasState

    data class Content(
        val prompt: String,
        val type: ExtraType,
        val loras: List<ExtraItemUi> = emptyList(),
    ) : ExtrasState
}

data class ExtraItemUi(
    val type: ExtraType,
    val key: String,
    val name: String,
    val alias: String?,
    val isApplied: Boolean,
    val value: String? = null,
)
