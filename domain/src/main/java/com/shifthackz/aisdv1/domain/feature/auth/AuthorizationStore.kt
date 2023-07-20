package com.shifthackz.aisdv1.domain.feature.auth

interface AuthorizationStore {
    fun getAuthorizationCredentials(): AuthorizationCredentials
    fun storeAuthorizationCredentials(credentials: AuthorizationCredentials)
}
