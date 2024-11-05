package com.shifthackz.aisdv1.domain.usecase.report

import com.shifthackz.aisdv1.domain.entity.ReportReason
import io.reactivex.rxjava3.core.Completable

interface SendReportUseCase {
    operator fun invoke(text: String, reason: ReportReason, image: String): Completable
}
