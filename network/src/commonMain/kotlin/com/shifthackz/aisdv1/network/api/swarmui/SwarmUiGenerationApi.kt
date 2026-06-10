package com.shifthackz.aisdv1.network.api.swarmui

import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.response.KtorSwarmUiGenerationResponse

interface SwarmUiGenerationApi {

    suspend fun generate(
        baseUrl: String,
        request: SwarmUiGenerationRequest,
        authorization: BasicHttpAuthorization?,
    ): KtorSwarmUiGenerationResponse

    suspend fun downloadImage(
        url: String,
        authorization: BasicHttpAuthorization?,
    ): ByteArray
}
