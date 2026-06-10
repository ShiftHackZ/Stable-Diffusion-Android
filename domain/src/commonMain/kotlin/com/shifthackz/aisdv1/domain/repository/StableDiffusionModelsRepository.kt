package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

/**
 * Defines the `StableDiffusionModelsRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StableDiffusionModelsRepository {
    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchModels()
    /**
     * Loads SDAI data through `fetchAndGetModels`.
     *
     * @return Result produced by `fetchAndGetModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetModels(): List<StableDiffusionModel>
    /**
     * Loads SDAI data through `getModels`.
     *
     * @return Result produced by `getModels`.
     * @author Dmitriy Moroz
     */
    suspend fun getModels(): List<StableDiffusionModel>
}
