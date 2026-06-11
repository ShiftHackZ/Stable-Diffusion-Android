package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.CoreMlGenerationRepository

/**
 * Implements `ObserveCoreMlProcessStatusUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ObserveCoreMlProcessStatusUseCaseImpl(
    /**
     * Exposes the `coreMlGenerationRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val coreMlGenerationRepository: CoreMlGenerationRepository,
) : ObserveCoreMlProcessStatusUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override fun invoke() = coreMlGenerationRepository.observeStatus()
}
