package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.core.ui.MviIntent

sealed interface EmbeddingIntent : MviIntent {

    data object Close : EmbeddingIntent

    data class ChangeSelector(val flag: Boolean) : EmbeddingIntent

    data class ToggleItem(val item: EmbeddingItemUi) : EmbeddingIntent

    data object ApplyNewPrompts : EmbeddingIntent
}
