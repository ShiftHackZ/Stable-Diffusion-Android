package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionScripts
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `StableDiffusionScriptsDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StableDiffusionScriptsDataSource {

    /**
     * Loads SDAI data through `fetchScripts`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchScripts`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchScripts(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): StableDiffusionScripts
}
