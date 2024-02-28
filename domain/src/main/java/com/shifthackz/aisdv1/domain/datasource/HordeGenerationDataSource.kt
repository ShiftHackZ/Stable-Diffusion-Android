package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

sealed interface HordeGenerationDataSource {
    interface Remote : HordeGenerationDataSource {
        fun validateApiKey(): Single<Boolean>
        fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult>
        fun imageToImage(payload: ImageToImagePayload): Single<AiGenerationResult>
        fun interruptGeneration(): Completable
    }

    interface StatusSource : HordeGenerationDataSource {
        var id: String?
        fun observe(): Flowable<HordeProcessStatus>
        fun update(status: HordeProcessStatus)
    }
}
