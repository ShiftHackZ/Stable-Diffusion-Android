package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

sealed interface HordeGenerationDataSource {
    interface Remote : HordeGenerationDataSource {
        fun textToImage(payload: TextToImagePayload): Single<AiGenerationResult>
        fun imageToImage(payload: ImageToImagePayload): Single<AiGenerationResult>
    }

    interface StatusSource : HordeGenerationDataSource {
        fun observe(): Flowable<HordeProcessStatus>
        fun update(status: HordeProcessStatus)
    }
}
