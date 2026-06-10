package com.shifthackz.aisdv1.storage.gateway

fun interface GatewayClearPersistentDb {
    suspend operator fun invoke()
}
