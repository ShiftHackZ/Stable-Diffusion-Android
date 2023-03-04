package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import io.reactivex.rxjava3.core.Single

interface TextToImageUseCase {
    operator fun invoke(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain>
}
