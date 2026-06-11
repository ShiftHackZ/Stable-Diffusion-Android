package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionScripts

/**
 * Defines the `StableDiffusionScriptsRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StableDiffusionScriptsRepository {

    /**
     * Loads SDAI data through `fetchScripts`.
     *
     * @return Result produced by `fetchScripts`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchScripts(): StableDiffusionScripts
}
