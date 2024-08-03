package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class TemporaryGenerationResultRepositoryImpl : TemporaryGenerationResultRepository {

    private var lastCachedResult: AiGenerationResult? = null

    override fun put(result: AiGenerationResult) = Completable.fromAction {
        lastCachedResult = result
    }

    override fun get(): Single<AiGenerationResult> {
        return lastCachedResult
            ?.let { Single.just(it) }
            ?: Single.error(IllegalStateException("No last cached result."))
    }
}
