package com.shifthackz.aisdv1.data.core

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

internal abstract class CoreGenerationRepository(
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    private val localDataSource: GenerationResultDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
) : CoreMediaStoreRepository(preferenceManager, mediaStoreGateway, base64ToBitmapConverter) {

    protected fun insertGenerationResult(ai: AiGenerationResult): Single<AiGenerationResult> {
        if (backgroundWorkObserver.hasActiveTasks() || preferenceManager.autoSaveAiResults) {
            return localDataSource
                .insert(ai)
                .flatMap { id -> exportToMediaStore(ai).andThen(Single.just(ai.copy(id))) }
        }
        return Single.just(ai)
    }
}
