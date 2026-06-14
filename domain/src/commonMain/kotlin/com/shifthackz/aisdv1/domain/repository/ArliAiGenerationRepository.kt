package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Generates images through the configured ArliAI account.
 *
 * Implementations resolve the selected checkpoint from the payload first and then from
 * persisted ArliAI settings, so callers can override the model per request.
 *
 * @author Dmitriy Moroz
 */
interface ArliAiGenerationRepository {
    /**
     * Checks whether the persisted ArliAI API key is accepted by the provider.
     *
     * @return `true` when ArliAI accepts the current key.
     *
     * @author Dmitriy Moroz
     */
    suspend fun validateApiKey(): Boolean

    /**
     * Generates one or more text-to-image results.
     *
     * @param payload generation settings and optional ArliAI model override.
     * @return persisted generation records produced from the provider response.
     *
     * @author Dmitriy Moroz
     */
    suspend fun generateFromText(payload: TextToImagePayload): List<AiGenerationResult>

    /**
     * Generates one or more image-to-image results.
     *
     * @param payload generation settings, source image data, and optional ArliAI model override.
     * @return persisted generation records produced from the provider response.
     *
     * @author Dmitriy Moroz
     */
    suspend fun generateFromImage(payload: ImageToImagePayload): List<AiGenerationResult>
}
