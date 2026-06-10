package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `ExtrasIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ExtrasIntent : MviIntent {

    /**
     * Carries `ToggleItem` data through the SDAI presentation layer.
     *
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ToggleItem(val item: ExtraItemUi) : ExtrasIntent

    /**
     * Provides the `ApplyPrompts` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ApplyPrompts : ExtrasIntent

    /**
     * Provides the `Close` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Close : ExtrasIntent
}
