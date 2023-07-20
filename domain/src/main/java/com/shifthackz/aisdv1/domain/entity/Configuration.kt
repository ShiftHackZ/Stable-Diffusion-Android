package com.shifthackz.aisdv1.domain.entity

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

data class Configuration(
    val serverUrl: String,
    val demoMode: Boolean,
    val source: ServerSource,
    val hordeApiKey: String,
    val authCredentials: AuthorizationCredentials,
)
