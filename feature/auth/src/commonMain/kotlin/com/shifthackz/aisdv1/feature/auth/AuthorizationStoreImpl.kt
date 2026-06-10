package com.shifthackz.aisdv1.feature.auth

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore

/**
 * Implements `AuthorizationStore` behavior in the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class AuthorizationStoreImpl(
    /**
     * Exposes the `store` value used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val store: AuthorizationKeyValueStore,
) : AuthorizationStore {

    /**
     * Loads SDAI data through `getAuthorizationCredentials`.
     *
     * @return Result produced by `getAuthorizationCredentials`.
     * @author Dmitriy Moroz
     */
    override fun getAuthorizationCredentials(): AuthorizationCredentials {
        val typeKey = getCredentialsTypeKey()
        return getCredentials(typeKey)
    }

    /**
     * Executes the `storeAuthorizationCredentials` step in the SDAI authentication feature layer.
     *
     * @param credentials credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun storeAuthorizationCredentials(credentials: AuthorizationCredentials) {
        saveCredentialsTypeKey(credentials.key)
        saveCredentials(credentials)
    }

    /**
     * Loads SDAI data through `getCredentialsTypeKey`.
     *
     * @return Result produced by `getCredentialsTypeKey`.
     * @author Dmitriy Moroz
     */
    private fun getCredentialsTypeKey(): AuthorizationCredentials.Key {
        val rawKey = store.getString(KEY_CREDENTIALS_TYPE).orEmpty()
        if (rawKey.isEmpty()) return AuthorizationCredentials.Key.NONE
        return AuthorizationCredentials.Key.from(rawKey)
    }

    /**
     * Performs the SDAI side effect handled by `saveCredentialsTypeKey`.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun saveCredentialsTypeKey(key: AuthorizationCredentials.Key) {
        store.putString(KEY_CREDENTIALS_TYPE, key.key)
    }

    /**
     * Loads SDAI data through `getCredentials`.
     *
     * @param key key value consumed by the API.
     * @return Result produced by `getCredentials`.
     * @author Dmitriy Moroz
     */
    private fun getCredentials(key: AuthorizationCredentials.Key): AuthorizationCredentials {
        val rawValue = store.getString(KEY_CREDENTIALS_RAW).orEmpty()
        if (rawValue.isEmpty()) return AuthorizationCredentials.None
        return try {
            val rawCredentials = parseByKeyValueToRaw(key, rawValue)
            rawCredentials.toDomain()
        } catch (_: Exception) {
            AuthorizationCredentials.None
        }
    }

    /**
     * Performs the SDAI side effect handled by `saveCredentials`.
     *
     * @param credentials credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun saveCredentials(credentials: AuthorizationCredentials) {
        val rawCredentials = credentials.toRaw()
        val rawValue = rawCredentials.toJson()
        store.putString(KEY_CREDENTIALS_RAW, rawValue)
    }

    /**
     * Provides the `companion object` singleton used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `KEY_CREDENTIALS_TYPE` value used by the SDAI authentication feature layer.
         *
         * @author Dmitriy Moroz
         */
        private const val KEY_CREDENTIALS_TYPE = "key_credentials_type"
        /**
         * Exposes the `KEY_CREDENTIALS_RAW` value used by the SDAI authentication feature layer.
         *
         * @author Dmitriy Moroz
         */
        private const val KEY_CREDENTIALS_RAW = "key_credentials_raw"
    }
}
