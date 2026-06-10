package com.shifthackz.aisdv1.domain.usecase.report

import com.shifthackz.aisdv1.domain.entity.ReportReason

interface SendReportUseCase {
    suspend operator fun invoke(text: String, reason: ReportReason, image: String)
}
