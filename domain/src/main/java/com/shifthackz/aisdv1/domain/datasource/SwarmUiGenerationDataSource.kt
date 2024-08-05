package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

sealed interface SwarmUiGenerationDataSource {

    interface Remote : SwarmUiGenerationDataSource {
        fun textToImage(
            sessionId: String,
            model: String,
            payload: TextToImagePayload,
        ): Single<AiGenerationResult>

        fun imageToImage(
            sessionId: String,
            model: String,
            payload: ImageToImagePayload,
        ): Single<AiGenerationResult>
    }
}
