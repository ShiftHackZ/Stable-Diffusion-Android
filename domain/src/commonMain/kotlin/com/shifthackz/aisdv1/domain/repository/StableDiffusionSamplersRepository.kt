package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler

/**
 * Defines the `StableDiffusionSamplersRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StableDiffusionSamplersRepository {
    /**
     * Loads SDAI data through `fetchSamplers`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchSamplers()

    /**
     * Loads SDAI data through `getSamplers`.
     *
     * @return Result produced by `getSamplers`.
     * @author Dmitriy Moroz
     */
    suspend fun getSamplers(): List<StableDiffusionSampler>
}
