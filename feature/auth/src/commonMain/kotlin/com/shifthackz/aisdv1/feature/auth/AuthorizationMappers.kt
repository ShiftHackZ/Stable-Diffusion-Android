package com.shifthackz.aisdv1.feature.auth

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.feature.auth.credentials.Credentials
import com.shifthackz.aisdv1.feature.auth.credentials.EmptyCredentials
import com.shifthackz.aisdv1.feature.auth.credentials.HttpBasicCredentials

/**
 * Converts SDAI data with `toRaw`.
 *
 * @author Dmitriy Moroz
 */
internal fun AuthorizationCredentials.toRaw(): Credentials = when (this) {
    is AuthorizationCredentials.HttpBasic -> HttpBasicCredentials(
        login = login,
        password = password,
    )
    else -> EmptyCredentials()
}

/**
 * Converts SDAI data with `toDomain`.
 *
 * @author Dmitriy Moroz
 */
internal fun Credentials.toDomain(): AuthorizationCredentials = when (this) {
    is HttpBasicCredentials -> AuthorizationCredentials.HttpBasic(
        login = login,
        password = password,
    )
    else -> AuthorizationCredentials.None
}

/**
 * Executes the `parseByKeyValueToRaw` step in the SDAI authentication feature layer.
 *
 * @param key key value consumed by the API.
 * @param rawValue raw value value consumed by the API.
 * @author Dmitriy Moroz
 */
internal fun parseByKeyValueToRaw(
    key: AuthorizationCredentials.Key,
    rawValue: String,
): Credentials = when (key) {
    AuthorizationCredentials.Key.NONE -> EmptyCredentials()
    AuthorizationCredentials.Key.HTTP_BASIC -> HttpBasicCredentials.fromJson(rawValue)
}
