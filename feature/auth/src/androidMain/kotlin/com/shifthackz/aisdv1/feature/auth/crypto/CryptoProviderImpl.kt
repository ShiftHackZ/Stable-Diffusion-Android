package com.shifthackz.aisdv1.feature.auth.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.shifthackz.aisdv1.feature.auth.AuthorizationKeyValueStore

internal class CryptoProviderImpl(private val context: Context) : CryptoProvider {

    override fun getAuthorizationStore(): AuthorizationKeyValueStore {
        val preferences = EncryptedSharedPreferences.create(
            KEY_PREFERENCE_AUTHORIZATION,
            getMasterKeyAlias(),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        return SharedPreferencesAuthorizationKeyValueStore(preferences)
    }

    private fun getMasterKeyAlias() = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    companion object {
        private const val KEY_PREFERENCE_AUTHORIZATION = "sdai_authorization_preference"
    }
}

private class SharedPreferencesAuthorizationKeyValueStore(
    private val preferences: SharedPreferences,
) : AuthorizationKeyValueStore {
    override fun getString(key: String): String? = preferences.getString(key, null)

    override fun putString(key: String, value: String) {
        preferences
            .edit()
            .putString(key, value)
            .apply()
    }
}
