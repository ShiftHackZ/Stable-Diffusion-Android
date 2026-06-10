package com.shifthackz.aisdv1.presentation.modal.tag

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `EditTagIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface EditTagIntent : MviIntent {

    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : EditTagIntent

    /**
     * Carries `InitialData` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class InitialData(
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
        /**
         * Exposes the `tag` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val tag: String,
        /**
         * Exposes the `isNegative` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val isNegative: Boolean,
    ) : EditTagIntent

    /**
     * Carries `UpdateTag` data through the SDAI presentation layer.
     *
     * @param tag tag value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateTag(val tag: String) : EditTagIntent

    /**
     * Carries `UpdateValue` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateValue(val value: Double) : EditTagIntent

    /**
     * Coordinates `Value` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Value : EditTagIntent {
        Increment, Decrement;
    }

    /**
     * Coordinates `Action` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Action : EditTagIntent {
        Apply, Delete;
    }
}
