package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import io.reactivex.rxjava3.core.Single

interface ImageToImageUseCase {
    operator fun invoke(payload: ImageToImagePayload): Single<List<AiGenerationResult>>
}
