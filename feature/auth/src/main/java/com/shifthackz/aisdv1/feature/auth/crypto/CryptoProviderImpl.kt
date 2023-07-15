package com.shifthackz.aisdv1.feature.auth.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class CryptoProviderImpl(private val context: Context) : CryptoProvider {

    override fun getAuthorizationPreferences(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            KEY_PREFERENCE_AUTHORIZATION,
            getMasterKeyAlias(),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun getMasterKeyAlias() = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    companion object {
        private const val KEY_PREFERENCE_AUTHORIZATION = "sdai_authorization_preference"
    }
}
