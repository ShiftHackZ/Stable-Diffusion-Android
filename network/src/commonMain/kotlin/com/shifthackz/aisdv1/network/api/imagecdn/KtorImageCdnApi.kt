package com.shifthackz.aisdv1.network.api.imagecdn

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import kotlin.random.Random

class KtorImageCdnApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : ImageCdnApi {

    constructor(baseUrl: String) : this(
        httpClient = createConfiguredHttpClient(),
        baseUrl = baseUrl,
    )

    override suspend fun fetchRandomImageBytes(): ByteArray = Random
        .nextInt(MIN, MAX + 1)
        .toString()
        .let { size ->
            httpClient.get {
                url.takeFrom(baseUrl)
                url.appendPathSegments(size, size)
            }
        }
        .body()

    private companion object {
        const val MIN = 400
        const val MAX = 700
    }
}
