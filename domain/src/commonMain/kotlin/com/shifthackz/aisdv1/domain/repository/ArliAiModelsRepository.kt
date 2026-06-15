package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

/**
 * Synchronizes and reads ArliAI checkpoint metadata.
 *
 * Remote models are cached locally so setup and generation screens can keep showing the last
 * known ArliAI list when a refresh fails.
 *
 * @author Dmitriy Moroz
 */
interface ArliAiModelsRepository {
    /**
     * Refreshes ArliAI checkpoint metadata from the provider and stores it locally.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchModels()

    /**
     * Attempts a provider refresh and then returns the locally cached checkpoint list.
     *
     * @return cached ArliAI checkpoints after the refresh attempt.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetModels(): List<StableDiffusionModel>

    /**
     * Reads cached ArliAI checkpoint metadata.
     *
     * @return locally stored ArliAI checkpoints.
     *
     * @author Dmitriy Moroz
     */
    suspend fun getModels(): List<StableDiffusionModel>
}
