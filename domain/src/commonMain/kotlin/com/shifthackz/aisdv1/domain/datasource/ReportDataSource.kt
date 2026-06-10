package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ReportReason

sealed interface ReportDataSource {

    interface Remote : ReportDataSource {

        suspend fun send(
            text: String,
            reason: ReportReason,
            image: String,
            source: String,
            model: String,
        )
    }
}
