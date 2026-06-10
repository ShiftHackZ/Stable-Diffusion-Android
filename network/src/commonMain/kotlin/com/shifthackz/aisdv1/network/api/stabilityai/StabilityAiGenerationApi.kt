package com.shifthackz.aisdv1.network.api.stabilityai

import com.shifthackz.aisdv1.network.request.StabilityTextToImageRequest
import com.shifthackz.aisdv1.network.response.StabilityCreditsResponse
import com.shifthackz.aisdv1.network.response.StabilityGenerationResponse

interface StabilityAiGenerationApi {

    suspend fun validateBearerToken(apiKey: String)

    suspend fun fetchCredits(apiKey: String): StabilityCreditsResponse

    suspend fun textToImage(
        apiKey: String,
        engineId: String,
        request: StabilityTextToImageRequest,
    ): StabilityGenerationResponse

    suspend fun imageToImage(
        apiKey: String,
        engineId: String,
        imageBytes: ByteArray,
        parameters: Map<String, String>,
    ): StabilityGenerationResponse
}
