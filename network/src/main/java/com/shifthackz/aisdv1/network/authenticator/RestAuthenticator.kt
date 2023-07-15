package com.shifthackz.aisdv1.network.authenticator

import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.network.qualifiers.CredentialsProvider
import com.shifthackz.aisdv1.network.qualifiers.Headers
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
        if (request.header(Headers.AUTHORIZATION) != null) {
            debugLog("[HTTP BASIC] Seems like credentials are not valid")
            return null
        }
        debugLog("[HTTP BASIC] Executing with HTTP BASIC authorization")
        debugLog("[HTTP BASIC] LOGIN    : ${credentials.login}")
        debugLog("[HTTP BASIC] PASSWORD : ${credentials.password}")
        return request
            .newBuilder()
            .header(Headers.AUTHORIZATION, Credentials.basic(credentials.login, credentials.password))
            .build()
    }
}
