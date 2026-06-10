package com.shifthackz.aisdv1.data.core

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

internal abstract class CoreGenerationRepository(
    mediaStoreGateway: MediaStoreGateway,
    private val localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
) : CoreMediaStoreRepository(preferenceManager, mediaStoreGateway) {

    protected suspend fun insertGenerationResult(ai: AiGenerationResult): AiGenerationResult {
        if (backgroundWorkObserver.hasActiveTasks() || preferenceManager.autoSaveAiResults) {
            val id = localDataSource.insert(ai)
            exportToMediaStoreAsync(ai)
            return ai.copy(id = id)
        }
        return ai
    }
}
