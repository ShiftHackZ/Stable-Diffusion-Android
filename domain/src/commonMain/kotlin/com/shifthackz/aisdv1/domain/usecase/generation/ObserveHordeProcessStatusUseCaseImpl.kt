package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import kotlinx.coroutines.flow.distinctUntilChanged

internal class ObserveHordeProcessStatusUseCaseImpl(
    private val hordeGenerationRepository: HordeGenerationRepository,
) : ObserveHordeProcessStatusUseCase {

    override fun invoke() = hordeGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
