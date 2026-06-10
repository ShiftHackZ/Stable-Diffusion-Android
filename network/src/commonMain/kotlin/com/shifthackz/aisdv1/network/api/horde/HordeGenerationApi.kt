package com.shifthackz.aisdv1.network.api.horde

import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.response.HordeGenerationAsyncResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckFullResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckResponse
import com.shifthackz.aisdv1.network.response.HordeUserResponse

interface HordeGenerationApi {

    suspend fun generateAsync(
        apiKey: String,
        request: HordeGenerationAsyncRequest,
    ): HordeGenerationAsyncResponse

    suspend fun checkGeneration(
        apiKey: String,
        id: String,
    ): HordeGenerationCheckResponse

    suspend fun checkStatus(
        apiKey: String,
        id: String,
    ): HordeGenerationCheckFullResponse

    suspend fun checkHordeApiKey(apiKey: String): HordeUserResponse

    suspend fun cancelRequest(
        apiKey: String,
        requestId: String,
    )

    suspend fun downloadImage(url: String): ByteArray
}
