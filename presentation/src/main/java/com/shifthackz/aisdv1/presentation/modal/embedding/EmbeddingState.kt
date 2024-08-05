package com.shifthackz.aisdv1.presentation.modal.embedding

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class EmbeddingState(
    val loading: Boolean = true,
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    val error: ErrorState = ErrorState.None,
    val prompt: String = "",
    val negativePrompt: String = "",
    val embeddings: List<EmbeddingItemUi> = emptyList(),
    val selector: Boolean = false,
) : MviState

@Immutable
data class EmbeddingItemUi(
    val keyword: String,
    val isInPrompt: Boolean,
    val isInNegativePrompt: Boolean,
)
