package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.android.core.mvi.MviState

data class EmbeddingState(
    val loading: Boolean = true,
    val error: ErrorState = ErrorState.None,
    val prompt: String = "",
    val negativePrompt: String = "",
    val embeddings: List<EmbeddingItemUi> = emptyList(),
    val selector: Boolean = false,
) : MviState


data class EmbeddingItemUi(
    val keyword: String,
    val isInPrompt: Boolean,
    val isInNegativePrompt: Boolean,
)
