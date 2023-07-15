package com.shifthackz.aisdv1.feature.auth

import android.content.SharedPreferences
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.authorization.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.authorization.AuthorizationStore

internal class AuthorizationStoreImpl(
    private val preferences: SharedPreferences,
) : AuthorizationStore {

    override fun getAuthorizationCredentials(): AuthorizationCredentials {
        val typeKey = getCredentialsTypeKey()
        val credentials = getCredentials(typeKey)
        debugLog("READ -> $credentials")
        return credentials
    }

    override fun storeAuthorizationCredentials(credentials: AuthorizationCredentials) {
        debugLog("WRITE -> $credentials")
        saveCredentialsTypeKey(credentials.key)
        saveCredentials(credentials)
    }

    private fun getCredentialsTypeKey(): AuthorizationCredentials.Key {
        val rawKey = preferences.getString(KEY_CREDENTIALS_TYPE, "") ?: ""
        if (rawKey.isEmpty()) return AuthorizationCredentials.Key.NONE
        return AuthorizationCredentials.Key.from(rawKey)
    }

    private fun saveCredentialsTypeKey(key: AuthorizationCredentials.Key) = preferences
        .edit()
        .putString(KEY_CREDENTIALS_TYPE, key.key)
        .apply()

    private fun getCredentials(key: AuthorizationCredentials.Key): AuthorizationCredentials {
        val rawValue = preferences.getString(KEY_CREDENTIALS_RAW, "") ?: ""
        if (rawValue.isEmpty()) return AuthorizationCredentials.None
        return try {
            val rawCredentials = parseByKeyValueToRaw(key, rawValue)
            rawCredentials.toDomain()
        } catch (e: Exception) {
            errorLog(e)
            AuthorizationCredentials.None
        }
    }

    private fun saveCredentials(credentials: AuthorizationCredentials) {
        val rawCredentials = credentials.toRaw();
        val rawValue = rawCredentials.toJson()
        return preferences
            .edit()
            .putString(KEY_CREDENTIALS_RAW, rawValue)
            .apply()
    }

    companion object {
        private const val KEY_CREDENTIALS_TYPE = "key_credentials_type"
        private const val KEY_CREDENTIALS_RAW = "key_credentials_raw"
    }
}
