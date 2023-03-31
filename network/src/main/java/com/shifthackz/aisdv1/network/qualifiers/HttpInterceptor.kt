package com.shifthackz.aisdv1.network.qualifiers

import okhttp3.Interceptor

internal data class HttpInterceptors(val interceptors: List<HttpInterceptor>)

@JvmInline
internal value class HttpInterceptor(
    private val interceptor: Interceptor
) : Interceptor by interceptor {
    val type: String
        get() = interceptor::class.java.name
}
