package com.shifthackz.aisdv1.presentation.screen.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `ReportIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ReportIntent : MviIntent {
    /**
     * Carries `UpdateText` data through the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateText(val text: String) : ReportIntent
    /**
     * Carries `UpdateReason` data through the SDAI presentation layer.
     *
     * @param reason reason value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateReason(val reason: ReportReason) : ReportIntent
    /**
     * Provides the `Submit` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Submit : ReportIntent
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : ReportIntent
    /**
     * Provides the `DismissError` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissError : ReportIntent
}
