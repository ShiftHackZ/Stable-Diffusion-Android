package com.shifthackz.aisdv1.feature.auth.di

import com.shifthackz.aisdv1.domain.authorization.AuthorizationStore
import com.shifthackz.aisdv1.feature.auth.AuthorizationStoreImpl
import com.shifthackz.aisdv1.feature.auth.crypto.CryptoProvider
import com.shifthackz.aisdv1.feature.auth.crypto.CryptoProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {
    factory<CryptoProvider> { CryptoProviderImpl(androidContext()) }
    factory<AuthorizationStore> {
        val encryptedPreferences = get<CryptoProvider>().getAuthorizationPreferences()
        AuthorizationStoreImpl(encryptedPreferences)
    }
}
