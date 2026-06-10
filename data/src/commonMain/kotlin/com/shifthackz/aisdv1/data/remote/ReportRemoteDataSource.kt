package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.ReportDataSource
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi
import com.shifthackz.aisdv1.network.request.ReportRequest

/**
 * Coordinates `ReportRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ReportRemoteDataSource(private val api: SdaiAppApi) : ReportDataSource.Remote {

    /**
     * Executes the `send` step in the SDAI data layer.
     *
     * @param text text value consumed by the API.
     * @param reason reason value consumed by the API.
     * @param image image value consumed by the API.
     * @param source source value consumed by the API.
     * @param model model value consumed by the API.
     * @author Dmitriy Moroz
     */
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
