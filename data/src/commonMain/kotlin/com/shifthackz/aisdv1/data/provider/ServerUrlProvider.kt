package com.shifthackz.aisdv1.data.provider

fun interface ServerUrlProvider {
    suspend operator fun invoke(endpoint: String): String
}
