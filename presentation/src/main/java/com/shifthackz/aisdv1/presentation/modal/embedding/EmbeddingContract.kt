package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

data class EmbeddingState(
    val loading: Boolean = true,
    val prompt: String = "",
    val negativePrompt: String = "",
    val embeddings: List<EmbeddingItemUi> = emptyList(),
    val selector: Boolean = false,
) : MviState

sealed interface EmbeddingEffect : MviEffect {
    data class ApplyPrompts(
        val prompt: String,
        val negativePrompt: String,
    ) : EmbeddingEffect
}

data class EmbeddingItemUi(
    val keyword: String,
    val isInPrompt: Boolean,
    val isInNegativePrompt: Boolean,
)
