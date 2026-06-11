package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ForgeModule

/**
 * Defines the `ForgeModulesRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ForgeModulesRepository {

    /**
     * Loads SDAI data through `fetchModules`.
     *
     * @return Result produced by `fetchModules`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchModules(): List<ForgeModule>
}
