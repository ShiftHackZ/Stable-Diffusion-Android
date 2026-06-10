package com.shifthackz.aisdv1.data.preference

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore

/**
 * Coordinates `KeyValueAuthorizationStore` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class KeyValueAuthorizationStore(
    /**
     * Exposes the `keyValueStore` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val keyValueStore: KeyValueStore,
) : AuthorizationStore {

    /**
     * Loads SDAI data through `getAuthorizationCredentials`.
     *
     * @return Result produced by `getAuthorizationCredentials`.
     * @author Dmitriy Moroz
     */
    override fun getAuthorizationCredentials(): AuthorizationCredentials {
        return when (getCredentialsTypeKey()) {
            AuthorizationCredentials.Key.NONE -> AuthorizationCredentials.None
            AuthorizationCredentials.Key.HTTP_BASIC -> getHttpBasicCredentials()
        }
    }

    /**
     * Executes the `storeAuthorizationCredentials` step in the SDAI data layer.
     *
     * @param credentials credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
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

    /**
     * Loads SDAI data through `getCredentialsTypeKey`.
     *
     * @return Result produced by `getCredentialsTypeKey`.
     * @author Dmitriy Moroz
     */
    private fun getCredentialsTypeKey(): AuthorizationCredentials.Key =
        keyValueStore
            .getString(KEY_CREDENTIALS_TYPE)
            .takeIf(String::isNotEmpty)
            ?.let(AuthorizationCredentials.Key::from)
            ?: AuthorizationCredentials.Key.NONE

    /**
     * Loads SDAI data through `getHttpBasicCredentials`.
     *
     * @return Result produced by `getHttpBasicCredentials`.
     * @author Dmitriy Moroz
     */
    private fun getHttpBasicCredentials(): AuthorizationCredentials {
        val login = keyValueStore.getString(KEY_HTTP_BASIC_LOGIN)
        val password = keyValueStore.getString(KEY_HTTP_BASIC_PASSWORD)
        return if (login.isEmpty() && password.isEmpty()) {
            AuthorizationCredentials.None
        } else {
            AuthorizationCredentials.HttpBasic(login = login, password = password)
        }
    }

    /**
     * Provides the `companion object` singleton used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `KEY_CREDENTIALS_TYPE` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val KEY_CREDENTIALS_TYPE = "key_credentials_type"
        /**
         * Exposes the `KEY_HTTP_BASIC_LOGIN` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val KEY_HTTP_BASIC_LOGIN = "key_http_basic_login"
        /**
         * Exposes the `KEY_HTTP_BASIC_PASSWORD` value used by the SDAI data layer.
         *
         * @author Dmitriy Moroz
         */
        const val KEY_HTTP_BASIC_PASSWORD = "key_http_basic_password"
    }
}
