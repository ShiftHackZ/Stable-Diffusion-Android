package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

sealed interface OpenAiGenerationDataSource {
    interface Remote : OpenAiGenerationDataSource {
        fun validateApiKey(): Single<Boolean>
        fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult>
    }
}
