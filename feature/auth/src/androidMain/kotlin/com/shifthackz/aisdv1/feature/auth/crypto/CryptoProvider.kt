package com.shifthackz.aisdv1.feature.auth.crypto

import com.shifthackz.aisdv1.feature.auth.AuthorizationKeyValueStore

/**
 * Defines the `CryptoProvider` contract for the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
internal interface CryptoProvider {
    /**
     * Loads SDAI data through `getAuthorizationStore`.
     *
     * @return Result produced by `getAuthorizationStore`.
     * @author Dmitriy Moroz
     */
    fun getAuthorizationStore(): AuthorizationKeyValueStore
}
