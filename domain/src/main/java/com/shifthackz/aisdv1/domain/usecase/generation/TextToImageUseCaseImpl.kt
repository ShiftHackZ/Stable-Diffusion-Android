package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

class TextToImageUseCaseImpl(
    private val repository: StableDiffusionGenerationRepository,
) : TextToImageUseCase {

    override operator fun invoke(payload: TextToImagePayload) = repository.generateFromText(payload)
}
