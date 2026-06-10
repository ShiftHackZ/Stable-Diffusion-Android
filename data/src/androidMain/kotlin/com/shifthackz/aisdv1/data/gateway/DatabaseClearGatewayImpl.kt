package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb

internal class DatabaseClearGatewayImpl(
    private val gatewayClearCacheDb: GatewayClearCacheDb,
    private val gatewayClearPersistentDb: GatewayClearPersistentDb,
) : DatabaseClearGateway {

    override suspend fun clearSessionScopeDb() {
        gatewayClearCacheDb()
    }

    override suspend fun clearStorageScopeDb() {
        gatewayClearPersistentDb()
    }
}
