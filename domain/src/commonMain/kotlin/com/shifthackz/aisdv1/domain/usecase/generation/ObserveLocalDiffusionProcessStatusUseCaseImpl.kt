package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import kotlinx.coroutines.flow.distinctUntilChanged

internal class ObserveLocalDiffusionProcessStatusUseCaseImpl(
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
) : ObserveLocalDiffusionProcessStatusUseCase {

    override fun invoke() = localDiffusionGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
