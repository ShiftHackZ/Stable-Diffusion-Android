package com.shifthackz.aisdv1.presentation.modal.embedding

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `EmbeddingState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class EmbeddingState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `source` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: ErrorState = ErrorState.None,
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String = "",
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String = "",
    /**
     * Exposes the `embeddings` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val embeddings: List<EmbeddingItemUi> = emptyList(),
    /**
     * Exposes the `selector` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selector: Boolean = true,
) : MviState

/**
 * Carries `EmbeddingItemUi` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class EmbeddingItemUi(
    /**
     * Exposes the `keyword` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val keyword: String,
    /**
     * Exposes the `isInPrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isInPrompt: Boolean,
    /**
     * Exposes the `isInNegativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isInNegativePrompt: Boolean,
)
