package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Single

class TextToImageUseCaseImpl(
    private val repository: StableDiffusionTextToImageRepository,
) : TextToImageUseCase {

    override operator fun invoke(payload: TextToImagePayload): Single<AiGenerationResult> =
        repository.generateAndGetImage(payload)
}
