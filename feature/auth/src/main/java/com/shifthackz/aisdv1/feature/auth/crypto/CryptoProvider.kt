package com.shifthackz.aisdv1.feature.auth.crypto

import android.content.SharedPreferences

internal interface CryptoProvider {
    fun getAuthorizationPreferences(): SharedPreferences
}
