package com.shifthackz.aisdv1.feature.auth.credentials

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

internal data class HttpBasicCredentials(
    @SerializedName("login")
    val login: String,
    @SerializedName("password")
    val password: String,
) : Credentials {

    override fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): HttpBasicCredentials {
            return Gson().fromJson(json, HttpBasicCredentials::class.java)
        }
    }
}
