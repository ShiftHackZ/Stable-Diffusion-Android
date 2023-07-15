package com.shifthackz.aisdv1.feature.auth

import com.shifthackz.aisdv1.domain.authorization.AuthorizationCredentials
import com.shifthackz.aisdv1.feature.auth.credentials.Credentials
import com.shifthackz.aisdv1.feature.auth.credentials.EmptyCredentials
import com.shifthackz.aisdv1.feature.auth.credentials.HttpBasicCredentials

internal fun AuthorizationCredentials.toRaw(): Credentials = when (this) {
    is AuthorizationCredentials.HttpBasic -> HttpBasicCredentials(
        login = login,
        password = password,
    )
    else -> EmptyCredentials()
}

internal fun Credentials.toDomain(): AuthorizationCredentials = when {
    this is HttpBasicCredentials -> AuthorizationCredentials.HttpBasic(
        login = login,
        password = password,
    )
    else -> AuthorizationCredentials.None
}

internal fun parseByKeyValueToRaw(
    key: AuthorizationCredentials.Key,
    rawValue: String
): Credentials = when (key) {
    AuthorizationCredentials.Key.NONE -> EmptyCredentials()
    AuthorizationCredentials.Key.HTTP_BASIC -> HttpBasicCredentials.fromJson(rawValue)
}
