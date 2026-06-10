package com.shifthackz.aisdv1.presentation.screen.history

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

@Immutable
data class HistoryState(
    val loading: Boolean = true,
    val items: List<AiGenerationResult> = emptyList(),
    val error: String? = null,
) : MviState
