package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Defines the `GetAllGalleryUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetAllGalleryUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<AiGenerationResult>
    /**
     * Executes the `ids` step in the SDAI domain layer.
     *
     * @return Result produced by `ids`.
     * @author Dmitriy Moroz
     */
    suspend fun ids(): List<Long>
}
