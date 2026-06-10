package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `SwarmUiGenerationRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SwarmUiGenerationRepository {
    /**
     * Executes the `checkApiAvailability` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend fun checkApiAvailability()
    /**
     * Executes the `checkApiAvailability` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    suspend fun checkApiAvailability(url: String)
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
