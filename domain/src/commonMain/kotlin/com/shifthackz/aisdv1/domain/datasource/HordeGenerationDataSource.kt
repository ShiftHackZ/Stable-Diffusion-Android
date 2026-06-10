package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow

sealed interface HordeGenerationDataSource {
    interface Remote : HordeGenerationDataSource {
        suspend fun validateApiKey(apiKey: String): Boolean
        suspend fun textToImage(apiKey: String, payload: TextToImagePayload): AiGenerationResult
        suspend fun imageToImage(apiKey: String, payload: ImageToImagePayload): AiGenerationResult
        suspend fun interruptGeneration(apiKey: String)
    }

    interface StatusSource : HordeGenerationDataSource {
        var id: String?
        fun observe(): Flow<HordeProcessStatus>
        fun update(status: HordeProcessStatus)
    }
}
