package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

/**
 * Groups ArliAI model-list data sources.
 *
 * @author Dmitriy Moroz
 */
sealed interface ArliAiModelsDataSource {

    /**
     * Loads ArliAI checkpoint metadata from the provider.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : ArliAiModelsDataSource {
        /**
         * Fetches the checkpoint list available to the supplied API key.
         *
         * @param apiKey ArliAI API key entered by the user.
         * @return checkpoint metadata mapped into the domain model.
         *
         * @author Dmitriy Moroz
         */
        suspend fun fetchModels(apiKey: String): List<StableDiffusionModel>
    }

    /**
     * Stores ArliAI checkpoint metadata in the local cache.
     *
     * @author Dmitriy Moroz
     */
    interface Local : ArliAiModelsDataSource {
        /**
         * Reads cached ArliAI checkpoint metadata.
         *
         * @return locally stored checkpoint metadata.
         *
         * @author Dmitriy Moroz
         */
        suspend fun getModels(): List<StableDiffusionModel>

        /**
         * Replaces cached ArliAI checkpoint metadata.
         *
         * @param models checkpoint metadata returned by the provider.
         *
         * @author Dmitriy Moroz
         */
        suspend fun insertModels(models: List<StableDiffusionModel>)
    }
}
