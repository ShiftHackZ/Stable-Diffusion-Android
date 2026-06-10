package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

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
    suspend fun put(result: AiGenerationResult)

    /**
     * Returns an instance of [AiGenerationResult].
     *
     * @throws IllegalStateException when no last cached result exists.
     */
    suspend fun get(): AiGenerationResult
}
