package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.ReportDataSource
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi
import com.shifthackz.aisdv1.network.request.ReportRequest

internal class ReportRemoteDataSource(private val api: SdaiAppApi) : ReportDataSource.Remote {

    override suspend fun send(
        text: String,
        reason: ReportReason,
        image: String,
        source: String,
        model: String
    ) {
        val payload = ReportRequest(text, reason.toString(), image, source, model)
        api.postReport(payload)
    }
}
