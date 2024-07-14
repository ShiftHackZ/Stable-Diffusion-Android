package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository

internal class ObserveLocalDiffusionProcessStatusUseCaseImpl(
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
) : ObserveLocalDiffusionProcessStatusUseCase {

    override fun invoke() = localDiffusionGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
