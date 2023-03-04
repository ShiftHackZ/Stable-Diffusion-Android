package com.shifthackz.aisdv1.domain.usecase

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import io.reactivex.rxjava3.core.Single

interface TextToImageUseCase {
    fun generate(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain>
}
