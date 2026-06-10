package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Implements `ObserveHordeProcessStatusUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ObserveHordeProcessStatusUseCaseImpl(
    /**
     * Exposes the `hordeGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val hordeGenerationRepository: HordeGenerationRepository,
) : ObserveHordeProcessStatusUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = hordeGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
