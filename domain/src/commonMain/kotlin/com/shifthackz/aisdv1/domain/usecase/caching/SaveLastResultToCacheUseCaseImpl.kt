package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository

/**
 * Implements `SaveLastResultToCacheUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class SaveLastResultToCacheUseCaseImpl(
    /**
     * Exposes the `temporaryGenerationResultRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val temporaryGenerationResultRepository: TemporaryGenerationResultRepository,
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : SaveLastResultToCacheUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(result: AiGenerationResult): AiGenerationResult {
        if (preferenceManager.autoSaveAiResults || result.id > 0L) return result
        temporaryGenerationResultRepository.put(result)
        return result
    }
}
