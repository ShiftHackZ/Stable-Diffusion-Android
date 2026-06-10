package com.shifthackz.aisdv1.storage.gateway

fun interface GatewayClearCacheDb {
    suspend operator fun invoke()
}
