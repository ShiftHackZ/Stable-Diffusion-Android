package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetGenerationResultIdsUseCaseImpl(
    private val generationResultRepository: GenerationResultRepository,
) : GetGenerationResultIdsUseCase {

    override fun invoke() = generationResultRepository.getAllIds()
}
