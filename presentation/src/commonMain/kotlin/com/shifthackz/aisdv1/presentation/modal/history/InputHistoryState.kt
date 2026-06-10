package com.shifthackz.aisdv1.presentation.modal.history

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Carries `InputHistoryState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class InputHistoryState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `loadingNextPage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loadingNextPage: Boolean = false,
    /**
     * Exposes the `items` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val items: List<InputHistoryItemUi> = emptyList(),
    /**
     * Exposes the `nextPage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val nextPage: Int = 0,
    /**
     * Exposes the `canLoadMore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val canLoadMore: Boolean = true,
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: String? = null,
) : MviState

/**
 * Carries `InputHistoryItemUi` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class InputHistoryItemUi(
    /**
     * Exposes the `generationResult` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val generationResult: AiGenerationResult,
    /**
     * Exposes the `image` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val image: ImageBitmap? = null,
)
