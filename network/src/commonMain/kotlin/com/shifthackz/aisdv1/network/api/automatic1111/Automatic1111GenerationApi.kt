package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse


/**
 * Defines the `Automatic1111GenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface Automatic1111GenerationApi {

    /**
     * Executes the `healthCheck` step in the SDAI network layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun healthCheck(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    )

    /**
     * Executes the `textToImage` step in the SDAI network layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `textToImage`.
     * @author Dmitriy Moroz
     */
    suspend fun textToImage(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: TextToImageRequest,
    ): SdGenerationResponse

    /**
     * Executes the `imageToImage` step in the SDAI network layer.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `imageToImage`.
     * @author Dmitriy Moroz
     */
    suspend fun imageToImage(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ImageToImageRequest,
    ): SdGenerationResponse

    /**
     * Performs the SDAI side effect handled by `interrupt`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param authorization authorization value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun interrupt(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    )
}
