package com.shifthackz.aisdv1.domain.gateway

import io.reactivex.rxjava3.core.Completable

interface DatabaseClearGateway {
    fun clearSessionScopeDb(): Completable
    fun clearStorageScopeDb(): Completable
}
