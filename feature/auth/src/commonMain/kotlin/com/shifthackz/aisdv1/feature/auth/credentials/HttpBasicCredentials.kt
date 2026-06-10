package com.shifthackz.aisdv1.feature.auth.credentials

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class HttpBasicCredentials(
    @SerialName("login")
    val login: String,
    @SerialName("password")
    val password: String,
) : Credentials {

    override fun toJson(): String = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String): HttpBasicCredentials =
            Json.decodeFromString(json)
    }
}
