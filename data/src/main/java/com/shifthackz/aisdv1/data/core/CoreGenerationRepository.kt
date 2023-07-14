package com.shifthackz.aisdv1.data.core

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

internal abstract class CoreGenerationRepository(
    private val localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
) {

    protected fun insertGenerationResult(ai: AiGenerationResult): Single<AiGenerationResult> {
        if (!preferenceManager.autoSaveAiResults) return Single.just(ai)
        return localDataSource
            .insert(ai)
            .map { id -> ai.copy(id) }
    }
}
