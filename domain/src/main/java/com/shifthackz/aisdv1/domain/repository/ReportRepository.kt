package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ReportReason
import io.reactivex.rxjava3.core.Completable

interface ReportRepository {
    fun send(text: String, reason: ReportReason, image: String): Completable
}
