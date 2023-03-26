package com.shifthackz.aisdv1.network.interceptor

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.network.qualifiers.ApiUrlProvider
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor(
    private val buildInfoProvider: BuildInfoProvider,
    private val apiUrlProvider: ApiUrlProvider,
) {

    fun get() = HttpLoggingInterceptor { message ->
        val badPredicate = message.contains(apiUrlProvider.stableDiffusionCloudAiApiUrl)
//        if (!badPredicate)
            debugLog(HTTP_TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    companion object {
        private const val HTTP_TAG = "HTTP"
    }
}
