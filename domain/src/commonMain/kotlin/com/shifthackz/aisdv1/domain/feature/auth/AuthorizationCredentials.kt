package com.shifthackz.aisdv1.domain.feature.auth

/**
 * Defines the `AuthorizationCredentials` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface AuthorizationCredentials {

    /**
     * Exposes the `key` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val key: Key

    /**
     * Provides the `None` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : AuthorizationCredentials {
        /**
         * Exposes the `key` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        override val key: Key = Key.NONE
    }

    /**
     * Carries `HttpBasic` data through the SDAI domain layer.
     *
     * @param login login value consumed by the API.
     * @param password password value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class HttpBasic(val login: String, val password: String) : AuthorizationCredentials {
        /**
         * Exposes the `key` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        override val key: Key = Key.HTTP_BASIC
    }

    /**
     * Coordinates `Key` behavior in the SDAI domain layer.
     *
     * @param key key value consumed by the API.
     * @author Dmitriy Moroz
     */
    enum class Key(val key: String) {
        NONE("none"),
        HTTP_BASIC("http");

        /**
         * Provides the `companion object` singleton used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        companion object {
            /**
             * Executes the `from` step in the SDAI domain layer.
             *
             * @param value value value consumed by the API.
             * @author Dmitriy Moroz
             */
            fun from(value: String) = entries.find { it.key == value } ?: NONE
        }
    }
}
