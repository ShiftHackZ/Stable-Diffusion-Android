package com.shifthackz.aisdv1.feature.auth

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore

internal class AuthorizationStoreImpl(
    private val store: AuthorizationKeyValueStore,
) : AuthorizationStore {

    override fun getAuthorizationCredentials(): AuthorizationCredentials {
        val typeKey = getCredentialsTypeKey()
        return getCredentials(typeKey)
    }

    override fun storeAuthorizationCredentials(credentials: AuthorizationCredentials) {
        saveCredentialsTypeKey(credentials.key)
        saveCredentials(credentials)
    }

    private fun getCredentialsTypeKey(): AuthorizationCredentials.Key {
        val rawKey = store.getString(KEY_CREDENTIALS_TYPE).orEmpty()
        if (rawKey.isEmpty()) return AuthorizationCredentials.Key.NONE
        return AuthorizationCredentials.Key.from(rawKey)
    }

    private fun saveCredentialsTypeKey(key: AuthorizationCredentials.Key) {
        store.putString(KEY_CREDENTIALS_TYPE, key.key)
    }

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

    private fun saveCredentials(credentials: AuthorizationCredentials) {
        val rawCredentials = credentials.toRaw()
        val rawValue = rawCredentials.toJson()
        store.putString(KEY_CREDENTIALS_RAW, rawValue)
    }

    companion object {
        private const val KEY_CREDENTIALS_TYPE = "key_credentials_type"
        private const val KEY_CREDENTIALS_RAW = "key_credentials_raw"
    }
}
