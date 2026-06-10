package com.shifthackz.aisdv1.feature.auth.crypto

import com.shifthackz.aisdv1.feature.auth.AuthorizationKeyValueStore

internal interface CryptoProvider {
    fun getAuthorizationStore(): AuthorizationKeyValueStore
}
