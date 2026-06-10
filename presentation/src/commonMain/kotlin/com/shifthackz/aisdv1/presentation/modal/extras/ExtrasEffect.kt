package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * Defines the `ExtrasEffect` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ExtrasEffect : MviEffect {

    /**
     * Carries `ApplyPrompts` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class ApplyPrompts(
        /**
         * Exposes the `prompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val prompt: String,
        /**
         * Exposes the `negativePrompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val negativePrompt: String,
    ) : ExtrasEffect

    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : ExtrasEffect
}
