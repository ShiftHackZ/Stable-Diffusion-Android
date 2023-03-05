package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain
import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Single

class TextToImageUseCaseImpl(
    private val repository: StableDiffusionTextToImageRepository,
) : TextToImageUseCase {

    override operator fun invoke(payload: TextToImagePayloadDomain): Single<AiGenerationResultDomain> =
        repository.generateAndGetImage(payload)
}
