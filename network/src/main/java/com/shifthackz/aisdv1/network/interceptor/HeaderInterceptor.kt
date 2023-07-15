package com.shifthackz.aisdv1.network.interceptor

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.network.qualifiers.Headers
import com.shifthackz.aisdv1.network.qualifiers.HordeApiKeyProvider
import okhttp3.Interceptor
import okhttp3.Response

internal class HeaderInterceptor(
    private val buildInfoProvider: BuildInfoProvider,
    private val hordeApiKeyProvider: HordeApiKeyProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain
        .request()
        .newBuilder()
        .addHeader(Headers.APP_VERSION, buildInfoProvider.version.toString())
        .addHeader(Headers.API_KEY, hordeApiKeyProvider().takeIf(String::isNotEmpty) ?: "0000000000")
        .build()
        .let(chain::proceed)
}
