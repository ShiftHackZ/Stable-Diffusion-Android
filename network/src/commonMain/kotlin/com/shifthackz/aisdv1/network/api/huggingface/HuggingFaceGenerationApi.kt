package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest

interface HuggingFaceGenerationApi {

    suspend fun validateBearerToken(apiKey: String)

    suspend fun generate(
        apiKey: String,
        model: String,
        request: HuggingFaceGenerationRequest,
    ): ByteArray
}
