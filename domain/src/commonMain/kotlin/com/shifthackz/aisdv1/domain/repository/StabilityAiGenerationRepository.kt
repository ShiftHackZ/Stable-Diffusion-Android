package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `StabilityAiGenerationRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiGenerationRepository {
    /**
     * Executes the `validateApiKey` step in the SDAI domain layer.
     *
     * @return Result produced by `validateApiKey`.
     * @author Dmitriy Moroz
     */
    suspend fun validateApiKey(): Boolean
    /**
     * Executes the `generateFromText` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @return Result produced by `generateFromText`.
     * @author Dmitriy Moroz
     */
    suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult
    /**
     * Executes the `generateFromImage` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @return Result produced by `generateFromImage`.
     * @author Dmitriy Moroz
     */
    suspend fun generateFromImage(payload: ImageToImagePayload): AiGenerationResult
}
