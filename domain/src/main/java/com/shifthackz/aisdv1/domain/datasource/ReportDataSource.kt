package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ReportReason
import io.reactivex.rxjava3.core.Completable

sealed interface ReportDataSource {

    interface Remote : ReportDataSource {

        fun send(
            text: String,
            reason: ReportReason,
            image: String,
            source: String,
            model: String,
        ): Completable
    }
}
