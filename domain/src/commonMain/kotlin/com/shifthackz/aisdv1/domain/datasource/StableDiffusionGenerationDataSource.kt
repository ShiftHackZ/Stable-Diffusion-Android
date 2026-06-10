package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface StableDiffusionGenerationDataSource {
    interface Remote : StableDiffusionGenerationDataSource {
        suspend fun checkAvailability(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        )

        suspend fun textToImage(
            baseUrl: String,
            credentials: AuthorizationCredentials,
            payload: TextToImagePayload,
        ): List<AiGenerationResult>

        suspend fun imageToImage(
            baseUrl: String,
            credentials: AuthorizationCredentials,
            payload: ImageToImagePayload,
        ): List<AiGenerationResult>

        suspend fun interruptGeneration(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        )
    }
}
