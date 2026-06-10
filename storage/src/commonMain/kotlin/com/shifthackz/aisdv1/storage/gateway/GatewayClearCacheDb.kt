package com.shifthackz.aisdv1.storage.gateway

/**
 * Executes the `function` step in the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
fun interface GatewayClearCacheDb {
    suspend operator fun invoke()
}
