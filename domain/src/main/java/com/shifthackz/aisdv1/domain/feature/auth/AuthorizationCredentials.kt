package com.shifthackz.aisdv1.domain.feature.auth

sealed interface AuthorizationCredentials {

    val key: Key

    object None : AuthorizationCredentials {
        override val key: Key = Key.NONE
    }

    data class HttpBasic(val login: String, val password: String) : AuthorizationCredentials {
        override val key: Key = Key.HTTP_BASIC
    }

    enum class Key(val key: String) {
        NONE("none"),
        HTTP_BASIC("http");

        companion object {
            fun from(value: String) = values().find { it.key == value } ?: NONE
        }
    }
}
