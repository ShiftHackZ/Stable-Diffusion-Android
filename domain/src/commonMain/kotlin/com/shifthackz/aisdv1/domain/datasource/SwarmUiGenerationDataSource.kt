package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface SwarmUiGenerationDataSource {

    interface Remote : SwarmUiGenerationDataSource {
        suspend fun textToImage(
            baseUrl: String,
            sessionId: String,
            model: String,
            credentials: AuthorizationCredentials,
            payload: TextToImagePayload,
        ): AiGenerationResult

        suspend fun imageToImage(
            baseUrl: String,
            sessionId: String,
            model: String,
            credentials: AuthorizationCredentials,
            payload: ImageToImagePayload,
        ): AiGenerationResult
    }
}
