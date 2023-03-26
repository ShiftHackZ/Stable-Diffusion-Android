package com.shifthackz.aisdv1.network.interceptor

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.log.debugLog
import okhttp3.Interceptor
import okhttp3.Response

class NetworkChainHeaderInterceptor(
    private val buildInfoProvider: BuildInfoProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain
        .request()
        .newBuilder()
        .addHeader(HEADER_APP_VERSION, buildInfoProvider.version.toString())
        .build()
        .also {
            debugLog("hd -> ${it.headers}")
        }
        .let(chain::proceed)

    companion object {
        private const val HEADER_APP_VERSION = "X-App-Version"
    }
}