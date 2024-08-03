package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

sealed interface SwarmUiGenerationDataSource {

    interface Remote {
        fun getNewSession(): Single<String>
        fun getNewSession(url: String): Single<String>
        fun textToImage(sessionId: String, payload: TextToImagePayload): Single<AiGenerationResult>
    }
}
