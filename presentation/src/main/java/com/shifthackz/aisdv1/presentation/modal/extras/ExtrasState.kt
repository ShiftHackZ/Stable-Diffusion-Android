package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.android.core.mvi.MviState

data class ExtrasState(
    val loading: Boolean = true,
    val error: ErrorState = ErrorState.None,
    val prompt: String = "",
    val negativePrompt: String = "",
    val type: ExtraType = ExtraType.Lora,
    val loras: List<ExtraItemUi> = emptyList(),
) : MviState

data class ExtraItemUi(
    val type: ExtraType,
    val key: String,
    val name: String,
    val alias: String?,
    val isApplied: Boolean,
    val value: String? = null,
)
