package com.shifthackz.aisdv1.presentation.screen.web.webui

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `WebUiIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface WebUiIntent : MviIntent {
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : WebUiIntent
}
