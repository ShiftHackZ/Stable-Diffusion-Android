package com.shifthackz.aisdv1.domain.usecase.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.domain.repository.ReportRepository

class SendReportUseCaseImpl(
    private val repository: ReportRepository,
) : SendReportUseCase {

    override fun invoke(text: String, reason: ReportReason, image: String) =
        repository.send(text, reason, image)

}
