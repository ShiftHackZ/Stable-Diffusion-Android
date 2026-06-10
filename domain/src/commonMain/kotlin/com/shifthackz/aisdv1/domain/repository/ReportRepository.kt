package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ReportReason

interface ReportRepository {
    suspend fun send(text: String, reason: ReportReason, image: String)
}
