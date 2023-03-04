package com.shifthackz.aisdv1.domain.usecase

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Single

class TextToImageUseCaseImpl(
    private val repository: StableDiffusionTextToImageRepository,
) : TextToImageUseCase {

    override fun generate(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain> =
        repository.getImage(payload)
}
