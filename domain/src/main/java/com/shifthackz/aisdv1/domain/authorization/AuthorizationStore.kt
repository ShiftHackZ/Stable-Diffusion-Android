package com.shifthackz.aisdv1.domain.authorization

interface AuthorizationStore {
    fun getAuthorizationCredentials(): AuthorizationCredentials
    fun storeAuthorizationCredentials(credentials: AuthorizationCredentials)
}
