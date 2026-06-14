package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Groups ArliAI generation data sources.
 *
 * @author Dmitriy Moroz
 */
sealed interface ArliAiGenerationDataSource {
    /**
     * Sends ArliAI validation and generation requests to the network layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : ArliAiGenerationDataSource {
        /**
         * Checks whether ArliAI accepts the supplied API key.
         *
         * @param apiKey ArliAI API key entered by the user.
         * @return `true` when the provider accepts the key.
         *
         * @author Dmitriy Moroz
         */
        suspend fun validateApiKey(apiKey: String): Boolean

        /**
         * Generates text-to-image results with a resolved ArliAI checkpoint.
         *
         * @param apiKey ArliAI API key entered by the user.
         * @param model checkpoint name sent to ArliAI.
         * @param payload domain generation settings.
         * @return mapped generation records returned by the provider.
         *
         * @author Dmitriy Moroz
         */
        suspend fun textToImage(
            apiKey: String,
            model: String,
            payload: TextToImagePayload,
        ): List<AiGenerationResult>

        /**
         * Generates image-to-image results with a resolved ArliAI checkpoint.
         *
         * @param apiKey ArliAI API key entered by the user.
         * @param model checkpoint name sent to ArliAI.
         * @param payload domain generation settings and source image data.
         * @return mapped generation records returned by the provider.
         *
         * @author Dmitriy Moroz
         */
        suspend fun imageToImage(
            apiKey: String,
            model: String,
            payload: ImageToImagePayload,
        ): List<AiGenerationResult>
    }
}
