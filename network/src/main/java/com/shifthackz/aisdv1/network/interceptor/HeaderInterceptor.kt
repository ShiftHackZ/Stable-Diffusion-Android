package com.shifthackz.aisdv1.network.interceptor

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import okhttp3.Interceptor
import okhttp3.Response

internal class HeaderInterceptor(
    private val buildInfoProvider: BuildInfoProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain
        .request()
        .newBuilder()
        .addHeader(HEADER_APP_VERSION, buildInfoProvider.version.toString())
        .addHeader(HEADER_API_KEY, "0000000000")
        .build()
        .let(chain::proceed)

    companion object {
        private const val HEADER_APP_VERSION = "X-App-Version"
        private const val HEADER_API_KEY = "apikey"
    }
}
