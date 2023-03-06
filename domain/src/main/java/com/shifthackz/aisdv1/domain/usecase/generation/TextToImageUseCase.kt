package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

interface TextToImageUseCase {
    operator fun invoke(payload: TextToImagePayload): Single<AiGenerationResult>
}
