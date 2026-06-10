package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.model.SupporterRaw
import com.shifthackz.aisdv1.network.request.ReportRequest
import com.shifthackz.aisdv1.network.response.DownloadableModelResponse

/**
 * Defines the `SdaiAppApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface SdaiAppApi {

    /**
     * Loads SDAI data through `fetchSupporters`.
     *
     * @return Result produced by `fetchSupporters`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchSupporters(): List<SupporterRaw>

    /**
     * Loads SDAI data through `fetchOnnxModels`.
     *
     * @return Result produced by `fetchOnnxModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchOnnxModels(): List<DownloadableModelResponse>

    /**
     * Loads SDAI data through `fetchMediaPipeModels`.
     *
     * @return Result produced by `fetchMediaPipeModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchMediaPipeModels(): List<DownloadableModelResponse>

    /**
     * Executes the `postReport` step in the SDAI network layer.
     *
     * @param request request value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun postReport(request: ReportRequest)
}
