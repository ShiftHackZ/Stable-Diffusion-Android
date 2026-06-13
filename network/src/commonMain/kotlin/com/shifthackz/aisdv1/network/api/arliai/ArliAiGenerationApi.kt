package com.shifthackz.aisdv1.network.api.arliai

import com.shifthackz.aisdv1.network.model.KtorStableDiffusionModelRaw
import com.shifthackz.aisdv1.network.request.ArliAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.ArliAiTextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse

/**
 * Defines the `ArliAiGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface ArliAiGenerationApi {
    suspend fun validateApiKey(apiKey: String)

    suspend fun fetchModels(apiKey: String): List<KtorStableDiffusionModelRaw>

    suspend fun textToImage(
        apiKey: String,
        request: ArliAiTextToImageRequest,
    ): SdGenerationResponse

    suspend fun imageToImage(
        apiKey: String,
        request: ArliAiImageToImageRequest,
    ): SdGenerationResponse
}
