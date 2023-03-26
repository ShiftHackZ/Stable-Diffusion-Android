package com.shifthackz.aisdv1.network.qualifiers

import okhttp3.Interceptor

data class HttpInterceptors(val interceptors: List<HttpInterceptor>)

@JvmInline
value class HttpInterceptor(
    private val interceptor: Interceptor
) : Interceptor by interceptor {
    val type: String
        get() = interceptor::class.java.name
}
