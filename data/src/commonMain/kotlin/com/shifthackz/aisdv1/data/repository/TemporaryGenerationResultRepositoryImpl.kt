package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository

/**
 * Implements `TemporaryGenerationResultRepository` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
internal class TemporaryGenerationResultRepositoryImpl : TemporaryGenerationResultRepository {

    /**
     * Exposes the `lastCachedResult` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private var lastCachedResult: AiGenerationResult? = null

    /**
     * Executes the `put` step in the SDAI data layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun put(result: AiGenerationResult) {
        lastCachedResult = result
    }

    /**
     * Loads SDAI data through `get`.
     *
     * @return Result produced by `get`.
     * @author Dmitriy Moroz
     */
    override suspend fun get(): AiGenerationResult =
        lastCachedResult ?: throw IllegalStateException("No last cached result.")
}
