package com.shifthackz.aisdv1.feature.auth

internal interface AuthorizationKeyValueStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}
