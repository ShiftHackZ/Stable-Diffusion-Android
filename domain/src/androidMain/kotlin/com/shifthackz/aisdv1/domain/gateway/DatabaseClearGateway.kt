package com.shifthackz.aisdv1.domain.gateway

interface DatabaseClearGateway {
    suspend fun clearSessionScopeDb()
    suspend fun clearStorageScopeDb()
}
