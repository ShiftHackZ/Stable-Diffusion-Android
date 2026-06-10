package com.shifthackz.aisdv1.network.api.horde

import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.response.HordeGenerationAsyncResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckFullResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckResponse
import com.shifthackz.aisdv1.network.response.HordeUserResponse

/**
 * Defines the `HordeGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface HordeGenerationApi {

    /**
     * Executes the `generateAsync` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `generateAsync`.
     * @author Dmitriy Moroz
     */
    suspend fun generateAsync(
        apiKey: String,
        request: HordeGenerationAsyncRequest,
    ): HordeGenerationAsyncResponse

    /**
     * Executes the `checkGeneration` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param id identifier of the target entity.
     * @return Result produced by `checkGeneration`.
     * @author Dmitriy Moroz
     */
    suspend fun checkGeneration(
        apiKey: String,
        id: String,
    ): HordeGenerationCheckResponse

    /**
     * Executes the `checkStatus` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param id identifier of the target entity.
     * @return Result produced by `checkStatus`.
     * @author Dmitriy Moroz
     */
    suspend fun checkStatus(
        apiKey: String,
        id: String,
    ): HordeGenerationCheckFullResponse

    /**
     * Executes the `checkHordeApiKey` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `checkHordeApiKey`.
     * @author Dmitriy Moroz
     */
    suspend fun checkHordeApiKey(apiKey: String): HordeUserResponse

    /**
     * Executes the `cancelRequest` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param requestId request id value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun cancelRequest(
        apiKey: String,
        requestId: String,
    )

    /**
     * Executes the `downloadImage` step in the SDAI network layer.
     *
     * @param url remote URL used by the operation.
     * @return Result produced by `downloadImage`.
     * @author Dmitriy Moroz
     */
    suspend fun downloadImage(url: String): ByteArray
}
