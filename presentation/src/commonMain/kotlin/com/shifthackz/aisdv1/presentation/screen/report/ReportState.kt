package com.shifthackz.aisdv1.presentation.screen.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.presentation.model.ErrorState

/**
 * Carries `ReportState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class ReportState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `error` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val error: ErrorState = ErrorState.None,
    /**
     * Exposes the `imageBase64` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val imageBase64: String = "",
    /**
     * Exposes the `text` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val text: String = "",
    /**
     * Exposes the `reason` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reason: ReportReason = ReportReason.Other,
    /**
     * Exposes the `reportSent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reportSent: Boolean = false,
) : MviState
