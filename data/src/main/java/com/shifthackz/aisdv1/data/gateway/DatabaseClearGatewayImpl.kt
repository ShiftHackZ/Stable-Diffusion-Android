package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb
import io.reactivex.rxjava3.core.Completable

internal class DatabaseClearGatewayImpl(
    private val gatewayClearCacheDb: GatewayClearCacheDb,
    private val gatewayClearPersistentDb: GatewayClearPersistentDb,
) : DatabaseClearGateway {

    override fun clearSessionScopeDb(): Completable = Completable.fromAction {
        gatewayClearCacheDb()
    }

    override fun clearStorageScopeDb(): Completable = Completable.fromAction {
        gatewayClearPersistentDb()
    }
}
