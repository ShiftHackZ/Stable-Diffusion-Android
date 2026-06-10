package com.shifthackz.aisdv1.feature.auth

/**
 * Defines the `AuthorizationKeyValueStore` contract for the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
internal interface AuthorizationKeyValueStore {
    /**
     * Loads SDAI data through `getString`.
     *
     * @param key key value consumed by the API.
     * @return Result produced by `getString`.
     * @author Dmitriy Moroz
     */
    fun getString(key: String): String?
    /**
     * Executes the `putString` step in the SDAI authentication feature layer.
     *
     * @param key key value consumed by the API.
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun putString(key: String, value: String)
}
