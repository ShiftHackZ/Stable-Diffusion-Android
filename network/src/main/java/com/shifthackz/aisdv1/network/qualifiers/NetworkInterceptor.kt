package com.shifthackz.aisdv1.network.qualifiers

import okhttp3.Interceptor

@JvmInline
value class NetworkInterceptor(
    private val interceptor: Interceptor,
) : Interceptor by interceptor {
    val type: String
        get() = interceptor::class.java.name
}
