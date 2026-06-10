package com.shifthackz.aisdv1.domain.usecase.caching

/**
 * Defines the `DataPreLoaderUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DataPreLoaderUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}

/**
 * Provides the `NoOpDataPreLoaderUseCase` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpDataPreLoaderUseCase : DataPreLoaderUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = Unit
}
