package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository

/**
 * Implements `GetLastResultFromCacheUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetLastResultFromCacheUseCaseImpl(
    /**
     * Exposes the `temporaryGenerationResultRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val temporaryGenerationResultRepository: TemporaryGenerationResultRepository,
) : GetLastResultFromCacheUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = temporaryGenerationResultRepository.get()
}
