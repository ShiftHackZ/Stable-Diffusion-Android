package com.shifthackz.aisdv1.network.qualifiers

interface CredentialsProvider {
    operator fun invoke(): Data

    sealed interface Data {
        data object None : Data
        data class HttpBasic(val login: String, val password: String) : Data
    }
}
