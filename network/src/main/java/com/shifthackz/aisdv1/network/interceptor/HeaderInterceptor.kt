package com.shifthackz.aisdv1.network.interceptor

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.extensions.applyIf
import com.shifthackz.aisdv1.network.qualifiers.NetworkHeaders
import com.shifthackz.aisdv1.network.qualifiers.ApiKeyProvider
import okhttp3.Interceptor
import okhttp3.Response

internal class HeaderInterceptor(
    private val buildInfoProvider: BuildInfoProvider,
    private val apiKeyProvider: ApiKeyProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain
        .request()
        .newBuilder()
        .addHeader(NetworkHeaders.APP_VERSION, buildInfoProvider.version.toString())
        .applyIf(apiKeyProvider() != null) {
            val (header, key) = apiKeyProvider.invoke() ?: ("" to "")
            addHeader(header, key)
        }
        .build()
        .let(chain::proceed)
}
