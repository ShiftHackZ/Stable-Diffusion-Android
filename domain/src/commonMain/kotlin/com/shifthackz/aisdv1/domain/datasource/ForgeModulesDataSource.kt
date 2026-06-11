package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `ForgeModulesDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ForgeModulesDataSource {

    /**
     * Loads SDAI data through `fetchModules`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchModules`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchModules(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): List<ForgeModule>
}
