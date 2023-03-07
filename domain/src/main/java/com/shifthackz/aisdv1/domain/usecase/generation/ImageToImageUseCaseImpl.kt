package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository

class ImageToImageUseCaseImpl(
    private val repository: StableDiffusionGenerationRepository,
) : ImageToImageUseCase {

    override fun invoke(payload: ImageToImagePayload) = repository.generateFromImage(payload)
}
