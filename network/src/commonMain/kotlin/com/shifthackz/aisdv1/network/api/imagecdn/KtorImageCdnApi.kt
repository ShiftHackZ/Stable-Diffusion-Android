package com.shifthackz.aisdv1.network.api.imagecdn

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import kotlin.random.Random

/**
 * Coordinates `KtorImageCdnApi` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class KtorImageCdnApi(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient,
    /**
     * Exposes the `baseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val baseUrl: String,
) : ImageCdnApi {

    /**
     * Creates a new SDAI component instance.
     *
     * @param baseUrl base url value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(baseUrl: String) : this(
        httpClient = createConfiguredHttpClient(),
        baseUrl = baseUrl,
    )

    /**
     * Loads SDAI data through `fetchRandomImageBytes`.
     *
     * @return Result produced by `fetchRandomImageBytes`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Provides the `companion object` singleton used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `MIN` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val MIN = 400
        /**
         * Exposes the `MAX` value used by the SDAI network layer.
         *
         * @author Dmitriy Moroz
         */
        const val MAX = 700
    }
}
