package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository

internal class SaveLastResultToCacheUseCaseImpl(
    private val temporaryGenerationResultRepository: TemporaryGenerationResultRepository,
    private val preferenceManager: PreferenceManager,
) : SaveLastResultToCacheUseCase {

    override suspend fun invoke(result: AiGenerationResult): AiGenerationResult {
        if (preferenceManager.autoSaveAiResults || result.id > 0L) return result
        temporaryGenerationResultRepository.put(result)
        return result
    }
}
