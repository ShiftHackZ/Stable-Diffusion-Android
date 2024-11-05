package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.ReportDataSource
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.network.api.sdai.ReportApi
import com.shifthackz.aisdv1.network.request.ReportRequest
import io.reactivex.rxjava3.core.Completable

internal class ReportRemoteDataSource(private val api: ReportApi) : ReportDataSource.Remote {

    override fun send(
        text: String,
        reason: ReportReason,
        image: String,
        source: String,
        model: String
    ): Completable {
        val payload = ReportRequest(text, reason.toString(), image, source, model)
        return api.postReport(payload)
    }
}
