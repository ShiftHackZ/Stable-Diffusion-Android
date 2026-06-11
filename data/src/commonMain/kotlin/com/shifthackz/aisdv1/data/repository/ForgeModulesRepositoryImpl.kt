package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ForgeModulesDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ForgeModulesRepository

/**
 * Implements `ForgeModulesRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ForgeModulesRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: ForgeModulesDataSource,
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
) : ForgeModulesRepository {

    /**
     * Loads SDAI data through `fetchModules`.
     *
     * @return Result produced by `fetchModules`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModules() = remoteDataSource.fetchModules(
        baseUrl = preferenceManager.automatic1111ServerUrl,
        credentials = authorizationStore.getAuthorizationCredentials(),
    )
}
