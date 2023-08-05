package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

/**
 * Used to store last [AiGenerationResult] in RAM, for the case when user did not save it to DB yet,
 * but it is needed to be loaded in GalleryDetail when accessing from result dialog.
 */
interface TemporaryGenerationResultRepository {
    /**
     * Saves the instance of last [AiGenerationResult] to RAM.
     *
     * @param result data of [AiGenerationResult] to save.
     */
    fun put(result: AiGenerationResult): Completable

    /**
     * Returns an instance of [AiGenerationResult] in [Single] source.
     *
     * @throws "No last cached result" error in [Single.error] source.
     */
    fun get(): Single<AiGenerationResult>
}
