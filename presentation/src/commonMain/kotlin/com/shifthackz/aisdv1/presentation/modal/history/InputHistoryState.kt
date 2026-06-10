package com.shifthackz.aisdv1.presentation.modal.history

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

@Immutable
data class InputHistoryState(
    val loading: Boolean = true,
    val loadingNextPage: Boolean = false,
    val items: List<InputHistoryItemUi> = emptyList(),
    val nextPage: Int = 0,
    val canLoadMore: Boolean = true,
    val error: String? = null,
) : MviState

@Immutable
data class InputHistoryItemUi(
    val generationResult: AiGenerationResult,
    val image: ImageBitmap? = null,
)
