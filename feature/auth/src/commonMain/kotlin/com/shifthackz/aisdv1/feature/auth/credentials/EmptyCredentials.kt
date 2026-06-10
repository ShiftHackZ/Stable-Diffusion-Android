package com.shifthackz.aisdv1.feature.auth.credentials

internal class EmptyCredentials : Credentials {
    override fun toJson(): String = ""
}
