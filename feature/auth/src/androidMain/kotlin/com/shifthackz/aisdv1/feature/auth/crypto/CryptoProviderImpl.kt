package com.shifthackz.aisdv1.feature.auth.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.shifthackz.aisdv1.feature.auth.AuthorizationKeyValueStore

/**
 * Implements `CryptoProvider` behavior in the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class CryptoProviderImpl(private val context: Context) : CryptoProvider {

    /**
     * Loads SDAI data through `getAuthorizationStore`.
     *
     * @return Result produced by `getAuthorizationStore`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Loads SDAI data through `getMasterKeyAlias`.
     *
     * @author Dmitriy Moroz
     */
    private fun getMasterKeyAlias() = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    /**
     * Provides the `companion object` singleton used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `KEY_PREFERENCE_AUTHORIZATION` value used by the SDAI authentication feature layer.
         *
         * @author Dmitriy Moroz
         */
        private const val KEY_PREFERENCE_AUTHORIZATION = "sdai_authorization_preference"
    }
}

/**
 * Coordinates `SharedPreferencesAuthorizationKeyValueStore` behavior in the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
private class SharedPreferencesAuthorizationKeyValueStore(
    /**
     * Exposes the `preferences` value used by the SDAI authentication feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferences: SharedPreferences,
) : AuthorizationKeyValueStore {
    /**
     * Loads SDAI data through `getString`.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun getString(key: String): String? = preferences.getString(key, null)

    /**
     * Executes the `putString` step in the SDAI authentication feature layer.
     *
     * @param key key value consumed by the API.
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun putString(key: String, value: String) {
        preferences
            .edit()
            .putString(key, value)
            .apply()
    }
}
