package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.model.SupporterRaw
import com.shifthackz.aisdv1.network.request.ReportRequest
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse

interface SdaiAppApi {

    suspend fun fetchSupporters(): List<SupporterRaw>

    suspend fun fetchOnnxModels(): List<DownloadableModelResponse>

    suspend fun fetchMediaPipeModels(): List<DownloadableModelResponse>

    suspend fun postReport(request: ReportRequest)
}
