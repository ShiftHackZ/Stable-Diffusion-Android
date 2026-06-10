package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Implements `ObserveLocalDiffusionProcessStatusUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ObserveLocalDiffusionProcessStatusUseCaseImpl(
    /**
     * Exposes the `localDiffusionGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
) : ObserveLocalDiffusionProcessStatusUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = localDiffusionGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
