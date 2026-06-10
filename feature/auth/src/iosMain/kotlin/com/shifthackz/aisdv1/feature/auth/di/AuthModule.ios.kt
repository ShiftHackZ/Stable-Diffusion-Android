package com.shifthackz.aisdv1.feature.auth.di

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.feature.auth.AppleAuthorizationKeyValueStore
import com.shifthackz.aisdv1.feature.auth.AuthorizationStoreImpl
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformAuthModule(): Module = module {
    factory<AuthorizationStore> {
        AuthorizationStoreImpl(AppleAuthorizationKeyValueStore())
    }
}
