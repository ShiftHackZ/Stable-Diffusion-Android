package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore

internal class KeyValueAuthorizationStore(
    private val keyValueStore: KeyValueStore,
) : AuthorizationStore {

    override fun getAuthorizationCredentials(): AuthorizationCredentials {
        return when (getCredentialsTypeKey()) {
            AuthorizationCredentials.Key.NONE -> AuthorizationCredentials.None
            AuthorizationCredentials.Key.HTTP_BASIC -> getHttpBasicCredentials()
        }
    }

    override fun storeAuthorizationCredentials(credentials: AuthorizationCredentials) {
        keyValueStore.putString(KEY_CREDENTIALS_TYPE, credentials.key.key)
        when (credentials) {
            is AuthorizationCredentials.HttpBasic -> {
                keyValueStore.putString(KEY_HTTP_BASIC_LOGIN, credentials.login)
                keyValueStore.putString(KEY_HTTP_BASIC_PASSWORD, credentials.password)
            }
            AuthorizationCredentials.None -> {
                keyValueStore.putString(KEY_HTTP_BASIC_LOGIN, "")
                keyValueStore.putString(KEY_HTTP_BASIC_PASSWORD, "")
            }
        }
    }

    private fun getCredentialsTypeKey(): AuthorizationCredentials.Key =
        keyValueStore
            .getString(KEY_CREDENTIALS_TYPE)
            .takeIf(String::isNotEmpty)
            ?.let(AuthorizationCredentials.Key::from)
            ?: AuthorizationCredentials.Key.NONE

    private fun getHttpBasicCredentials(): AuthorizationCredentials {
        val login = keyValueStore.getString(KEY_HTTP_BASIC_LOGIN)
        val password = keyValueStore.getString(KEY_HTTP_BASIC_PASSWORD)
        return if (login.isEmpty() && password.isEmpty()) {
            AuthorizationCredentials.None
        } else {
            AuthorizationCredentials.HttpBasic(login = login, password = password)
        }
    }

    private companion object {
        const val KEY_CREDENTIALS_TYPE = "key_credentials_type"
        const val KEY_HTTP_BASIC_LOGIN = "key_http_basic_login"
        const val KEY_HTTP_BASIC_PASSWORD = "key_http_basic_password"
    }
}
