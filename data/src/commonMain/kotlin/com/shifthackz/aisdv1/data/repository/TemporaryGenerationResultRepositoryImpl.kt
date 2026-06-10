package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository

internal class TemporaryGenerationResultRepositoryImpl : TemporaryGenerationResultRepository {

    private var lastCachedResult: AiGenerationResult? = null

    override suspend fun put(result: AiGenerationResult) {
        lastCachedResult = result
    }

    override suspend fun get(): AiGenerationResult =
        lastCachedResult ?: throw IllegalStateException("No last cached result.")
}
