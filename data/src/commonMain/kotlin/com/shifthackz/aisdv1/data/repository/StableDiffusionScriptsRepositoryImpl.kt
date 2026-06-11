package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionScriptsDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionScriptsRepository

/**
 * Implements `StableDiffusionScriptsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionScriptsRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StableDiffusionScriptsDataSource,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `authorizationStore` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
) : StableDiffusionScriptsRepository {

    /**
     * Loads SDAI data through `fetchScripts`.
     *
     * @return Result produced by `fetchScripts`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchScripts() = remoteDataSource.fetchScripts(
        baseUrl = preferenceManager.automatic1111ServerUrl,
        credentials = authorizationStore.getAuthorizationCredentials(),
    )
}
