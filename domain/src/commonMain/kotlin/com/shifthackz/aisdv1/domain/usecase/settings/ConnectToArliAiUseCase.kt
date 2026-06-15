package com.shifthackz.aisdv1.domain.usecase.settings

/**
 * Saves ArliAI setup data and verifies provider connectivity.
 *
 * @author Dmitriy Moroz
 */
fun interface ConnectToArliAiUseCase {
    /**
     * Attempts to connect to ArliAI with the supplied API key.
     *
     * @param apiKey ArliAI API key entered by the user.
     * @return success when the key is saved and accepted by ArliAI.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(apiKey: String): Result<Unit>
}
