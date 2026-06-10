package com.shifthackz.aisdv1.domain.usecase.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.domain.repository.ReportRepository

/**
 * Implements `SendReportUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class SendReportUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: ReportRepository,
) : SendReportUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param text text value consumed by the API.
     * @param reason reason value consumed by the API.
     * @param image image value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(text: String, reason: ReportReason, image: String) =
        repository.send(text, reason, image)

}
