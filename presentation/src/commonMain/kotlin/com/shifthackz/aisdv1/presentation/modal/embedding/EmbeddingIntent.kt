package com.shifthackz.aisdv1.presentation.modal.embedding

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `EmbeddingIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface EmbeddingIntent : MviIntent {

    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : EmbeddingIntent

    /**
     * Carries `ChangeSelector` data through the SDAI presentation layer.
     *
     * @param flag flag value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ChangeSelector(val flag: Boolean) : EmbeddingIntent

    /**
     * Carries `ToggleItem` data through the SDAI presentation layer.
     *
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ToggleItem(val item: EmbeddingItemUi) : EmbeddingIntent

    /**
     * Provides the `ApplyNewPrompts` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ApplyNewPrompts : EmbeddingIntent
}
