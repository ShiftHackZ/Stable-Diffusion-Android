package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.BonsaiGenerationRepository
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Implements `ObserveBonsaiProcessStatusUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ObserveBonsaiProcessStatusUseCaseImpl(
    /**
     * Exposes the `bonsaiGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val bonsaiGenerationRepository: BonsaiGenerationRepository,
) : ObserveBonsaiProcessStatusUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override fun invoke() = bonsaiGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
