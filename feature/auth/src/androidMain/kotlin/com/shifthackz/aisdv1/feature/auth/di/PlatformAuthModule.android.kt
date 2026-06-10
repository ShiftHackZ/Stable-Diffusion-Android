package com.shifthackz.aisdv1.feature.auth.di

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.feature.auth.AuthorizationStoreImpl
import com.shifthackz.aisdv1.feature.auth.crypto.CryptoProvider
import com.shifthackz.aisdv1.feature.auth.crypto.CryptoProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformAuthModule(): Module = module {
    factory<CryptoProvider> { CryptoProviderImpl(androidContext()) }
    factory<AuthorizationStore> {
        AuthorizationStoreImpl(get<CryptoProvider>().getAuthorizationStore())
    }
}
