package com.shifthackz.aisdv1.domain.gateway

/**
 * Defines the `DatabaseClearGateway` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DatabaseClearGateway {
    /**
     * Performs the SDAI side effect handled by `clearSessionScopeDb`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun clearSessionScopeDb()
    /**
     * Performs the SDAI side effect handled by `clearStorageScopeDb`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun clearStorageScopeDb()
}
