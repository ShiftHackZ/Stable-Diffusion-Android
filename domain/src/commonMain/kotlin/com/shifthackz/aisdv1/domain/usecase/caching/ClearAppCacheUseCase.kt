package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Defines the `ClearAppCacheUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ClearAppCacheUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}

/**
 * Implements `ClearAppCacheUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ClearAppCacheUseCaseImpl(
    /**
     * Exposes the `appCacheCleaner` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val appCacheCleaner: AppCacheCleaner,
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
) : ClearAppCacheUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() {
        repository.deleteAll()
        appCacheCleaner.clear()
    }
}

/**
 * Defines the `AppCacheCleaner` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface AppCacheCleaner {
    /**
     * Performs the SDAI side effect handled by `clear`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun clear()
}

/**
 * Provides the `NoOpAppCacheCleaner` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpAppCacheCleaner : AppCacheCleaner {
    /**
     * Performs the SDAI side effect handled by `clear`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun clear() = Unit
}
