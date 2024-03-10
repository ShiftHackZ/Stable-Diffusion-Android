package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

sealed interface StabilityAiGenerationDataSource {
    interface Remote : StabilityAiGenerationDataSource {
        fun validateApiKey(): Single<Boolean>
        fun fetchEngines(): Single<List<StabilityAiEngine>>
        fun textToImage(engineId: String, payload: TextToImagePayload): Single<AiGenerationResult>
        fun imageToImage(engineId: String, payload: ImageToImagePayload): Single<AiGenerationResult>
    }
}
