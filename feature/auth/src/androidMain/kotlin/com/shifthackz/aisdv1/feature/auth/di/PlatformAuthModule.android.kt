package com.shifthackz.aisdv1.feature.auth.di

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.feature.auth.AuthorizationStoreImpl
import com.shifthackz.aisdv1.feature.auth.crypto.CryptoProvider
import com.shifthackz.aisdv1.feature.auth.crypto.CryptoProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Executes the `platformAuthModule` step in the SDAI authentication feature layer.
 *
 * @return Result produced by `platformAuthModule`.
 * @author Dmitriy Moroz
 */
internal actual fun platformAuthModule(): Module = module {
    factory<CryptoProvider> { CryptoProviderImpl(androidContext()) }
    factory<AuthorizationStore> {
        AuthorizationStoreImpl(get<CryptoProvider>().getAuthorizationStore())
    }
}
