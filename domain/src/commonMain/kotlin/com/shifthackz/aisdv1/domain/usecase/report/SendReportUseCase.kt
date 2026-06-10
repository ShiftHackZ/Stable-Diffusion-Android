package com.shifthackz.aisdv1.domain.usecase.report

import com.shifthackz.aisdv1.domain.entity.ReportReason

/**
 * Defines the `SendReportUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SendReportUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param text text value consumed by the API.
     * @param reason reason value consumed by the API.
     * @param image image value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(text: String, reason: ReportReason, image: String)
}
