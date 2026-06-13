package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.StableDiffusionCppGenerationRepository

internal class ObserveStableDiffusionCppProcessStatusUseCaseImpl(
    private val stableDiffusionCppGenerationRepository: StableDiffusionCppGenerationRepository,
) : ObserveStableDiffusionCppProcessStatusUseCase {

    override fun invoke() = stableDiffusionCppGenerationRepository.observeStatus()
}
