package com.shifthackz.aisdv1.data.core

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Coordinates `CoreGenerationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal abstract class CoreGenerationRepository(
    mediaStoreGateway: MediaStoreGateway,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: GenerationResultDataSource.Local,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
) : CoreMediaStoreRepository(preferenceManager, mediaStoreGateway) {

    /**
     * Performs the SDAI side effect handled by `insertGenerationResult`.
     *
     * @param ai ai value consumed by the API.
     * @return Result produced by `insertGenerationResult`.
     * @author Dmitriy Moroz
     */
    protected suspend fun insertGenerationResult(ai: AiGenerationResult): AiGenerationResult {
        if (backgroundWorkObserver.hasActiveTasks() || preferenceManager.autoSaveAiResults) {
            val id = localDataSource.insert(ai)
            exportToMediaStoreAsync(ai)
            return ai.copy(id = id)
        }
        return ai
    }
}
