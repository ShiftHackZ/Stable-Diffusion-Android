package com.shifthackz.aisdv1.domain.usecase.connectivity

/**
 * Validates the ArliAI API key currently stored in setup configuration.
 *
 * @author Dmitriy Moroz
 */
fun interface TestArliAiApiKeyUseCase {
    /**
     * Checks whether the configured ArliAI key can reach provider endpoints.
     *
     * @return `true` when the stored key is accepted by ArliAI.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): Boolean
}
