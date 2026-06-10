package com.shifthackz.aisdv1.network.api.openai

import com.shifthackz.aisdv1.network.request.OpenAiRequest
import com.shifthackz.aisdv1.network.response.OpenAiResponse

interface OpenAiGenerationApi {

    suspend fun validateBearerToken(apiKey: String)

    suspend fun generateImage(apiKey: String, request: OpenAiRequest): OpenAiResponse
}
