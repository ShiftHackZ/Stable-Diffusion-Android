package com.shifthackz.aisdv1.network.interceptor

import com.shifthackz.aisdv1.core.common.log.debugLog
import okhttp3.logging.HttpLoggingInterceptor

internal class LoggingInterceptor {

    fun get(): HttpLoggingInterceptor = HttpLoggingInterceptor { message ->
        debugLog(HTTP_TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    companion object {
        private const val HTTP_TAG = "HTTP"
    }
}
