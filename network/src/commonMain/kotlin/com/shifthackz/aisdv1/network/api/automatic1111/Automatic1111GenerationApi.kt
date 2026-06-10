package com.shifthackz.aisdv1.network.api.automatic1111

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse


interface Automatic1111GenerationApi {

    suspend fun healthCheck(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    )

    suspend fun textToImage(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: TextToImageRequest,
    ): SdGenerationResponse

    suspend fun imageToImage(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
        request: ImageToImageRequest,
    ): SdGenerationResponse

    suspend fun interrupt(
        baseUrl: String,
        authorization: BasicHttpAuthorization?,
    )
}
