package com.shifthackz.aisdv1.domain.feature.auth

/**
 * Defines the `AuthorizationStore` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface AuthorizationStore {
    /**
     * Loads SDAI data through `getAuthorizationCredentials`.
     *
     * @return Result produced by `getAuthorizationCredentials`.
     * @author Dmitriy Moroz
     */
    fun getAuthorizationCredentials(): AuthorizationCredentials
    /**
     * Executes the `storeAuthorizationCredentials` step in the SDAI domain layer.
     *
     * @param credentials credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun storeAuthorizationCredentials(credentials: AuthorizationCredentials)
}
