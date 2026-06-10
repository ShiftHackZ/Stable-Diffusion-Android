package com.shifthackz.aisdv1.presentation.screen.history

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Carries `HistoryState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class HistoryState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `items` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val items: List<AiGenerationResult> = emptyList(),
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: String? = null,
) : MviState
