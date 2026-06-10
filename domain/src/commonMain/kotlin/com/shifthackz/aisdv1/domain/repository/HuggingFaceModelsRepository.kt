package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

/**
 * Defines the `HuggingFaceModelsRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface HuggingFaceModelsRepository {
    /**
     * Loads SDAI data through `fetchHuggingFaceModels`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchHuggingFaceModels()
    /**
     * Loads SDAI data through `fetchAndGetHuggingFaceModels`.
     *
     * @return Result produced by `fetchAndGetHuggingFaceModels`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetHuggingFaceModels(): List<HuggingFaceModel>
    /**
     * Loads SDAI data through `getHuggingFaceModels`.
     *
     * @return Result produced by `getHuggingFaceModels`.
     * @author Dmitriy Moroz
     */
    suspend fun getHuggingFaceModels(): List<HuggingFaceModel>
}
