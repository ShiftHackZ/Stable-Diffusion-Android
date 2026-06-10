package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb

/**
 * Implements `DatabaseClearGateway` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class DatabaseClearGatewayImpl(
    /**
     * Exposes the `gatewayClearCacheDb` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val gatewayClearCacheDb: GatewayClearCacheDb,
    /**
     * Exposes the `gatewayClearPersistentDb` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val gatewayClearPersistentDb: GatewayClearPersistentDb,
) : DatabaseClearGateway {

    /**
     * Performs the SDAI side effect handled by `clearSessionScopeDb`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun clearSessionScopeDb() {
        gatewayClearCacheDb()
    }

    /**
     * Performs the SDAI side effect handled by `clearStorageScopeDb`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun clearStorageScopeDb() {
        gatewayClearPersistentDb()
    }
}
