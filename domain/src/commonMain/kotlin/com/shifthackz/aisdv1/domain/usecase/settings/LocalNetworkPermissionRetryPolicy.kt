package com.shifthackz.aisdv1.domain.usecase.settings

import kotlinx.coroutines.delay

/**
 * Exposes the `localNetworkPermissionRetryEnabled` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal expect val localNetworkPermissionRetryEnabled: Boolean

/**
 * Executes the `withLocalNetworkPermissionRetry` step in the SDAI domain layer.
 *
 * @param url remote URL used by the operation.
 * @param block block value consumed by the API.
 * @return Result produced by `withLocalNetworkPermissionRetry`.
 * @throws Throwable when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
internal suspend fun <T> withLocalNetworkPermissionRetry(
    url: String,
    block: suspend () -> T,
): T {
    if (!localNetworkPermissionRetryEnabled || !url.isLikelyLocalNetworkEndpoint()) {
        return block()
    }

    var lastError: Throwable? = null
    repeat(LOCAL_NETWORK_RETRY_ATTEMPTS) { attempt ->
        try {
            return block()
        } catch (t: Throwable) {
            lastError = t
            if (!t.isLikelyLocalNetworkBootstrapError() || attempt == LOCAL_NETWORK_RETRY_ATTEMPTS - 1) {
                throw t
            }
            delay(LOCAL_NETWORK_RETRY_DELAYS_MILLIS.getOrElse(attempt) { LOCAL_NETWORK_RETRY_DELAYS_MILLIS.last() })
        }
    }
    throw lastError ?: IllegalStateException("Local network request failed.")
}

/**
 * Executes the `isLikelyLocalNetworkEndpoint` step in the SDAI domain layer.
 *
 * @return Result produced by `isLikelyLocalNetworkEndpoint`.
 * @author Dmitriy Moroz
 */
private fun String.isLikelyLocalNetworkEndpoint(): Boolean {
    val host = substringAfter("://", this)
        .substringBefore("/")
        .substringAfterLast("@")
        .substringBefore(":")
        .trim('[', ']')
        .lowercase()

    if (host.isBlank()) return false
    if (host == "localhost" || host.endsWith(".local") || host.endsWith(".lan") || host.endsWith(".home.arpa")) {
        return true
    }
    if (host.startsWith("10.") || host.startsWith("192.168.") || host.startsWith("127.") || host.startsWith("169.254.")) {
        return true
    }
    val parts = host.split('.')
    val first = parts.getOrNull(0)?.toIntOrNull()
    val second = parts.getOrNull(1)?.toIntOrNull()
    return first == 172 && second != null && second in 16..31
}

/**
 * Executes the `isLikelyLocalNetworkBootstrapError` step in the SDAI domain layer.
 *
 * @return Result produced by `isLikelyLocalNetworkBootstrapError`.
 * @author Dmitriy Moroz
 */
private fun Throwable.isLikelyLocalNetworkBootstrapError(): Boolean {
    val text = buildString {
        append(message.orEmpty())
        append(' ')
        append(cause?.message.orEmpty())
    }.lowercase()

    return listOf(
        "nsurlerrordomain",
        "darwinhttprequestexception",
        "could not connect",
        "connection refused",
        "network is unreachable",
        "no route to host",
        "no address associated",
        "-1003",
        "-1004",
        "-1009",
    ).any(text::contains)
}

/**
 * Exposes the `LOCAL_NETWORK_RETRY_ATTEMPTS` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
private const val LOCAL_NETWORK_RETRY_ATTEMPTS = 3
/**
 * Exposes the `LOCAL_NETWORK_RETRY_DELAYS_MILLIS` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
private val LOCAL_NETWORK_RETRY_DELAYS_MILLIS = listOf(1_500L, 3_000L)
