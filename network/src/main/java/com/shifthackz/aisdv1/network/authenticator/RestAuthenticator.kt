package com.shifthackz.aisdv1.network.authenticator

import com.shifthackz.aisdv1.network.qualifiers.CredentialsProvider
import com.shifthackz.aisdv1.network.qualifiers.NetworkHeaders
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

internal class RestAuthenticator(
    private val credentialsProvider: CredentialsProvider,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return when (val credentials = credentialsProvider()) {
            is CredentialsProvider.Data.HttpBasic -> httpAuthentication(response, credentials)
            else -> null
        }
    }

    private fun httpAuthentication(response: Response, credentials: CredentialsProvider.Data.HttpBasic): Request? {
        val request = response.request
        if (request.header(NetworkHeaders.AUTHORIZATION) != null) return null
        return request
            .newBuilder()
            .header(
                name = NetworkHeaders.AUTHORIZATION,
                value = Credentials.basic(credentials.login, credentials.password),
            )
            .build()
    }
}
