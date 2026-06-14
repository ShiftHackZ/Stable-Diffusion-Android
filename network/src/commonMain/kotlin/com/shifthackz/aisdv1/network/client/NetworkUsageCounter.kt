package com.shifthackz.aisdv1.network.client

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Ktor traffic buckets shown by Settings network usage.
 *
 * Model downloads are intentionally absent here because they are streamed by platform downloaders
 * and reported as byte progress through the domain repository.
 *
 * @author Dmitriy Moroz
 */
enum class NetworkUsageCategory {
    /**
     * Provider configuration, options, model-list, and similar sync requests.
     *
     * @author Dmitriy Moroz
     */
    CONFIGS,

    /**
     * Generation and inference requests, including request payloads and received responses.
     *
     * @author Dmitriy Moroz
     */
    INFERENCE,
}

/**
 * Request attribute used to mark a Ktor call for usage accounting.
 *
 * @author Dmitriy Moroz
 */
internal val NetworkUsageCategoryAttribute = AttributeKey<NetworkUsageCategory>("SDAI.NetworkUsageCategory")

/**
 * Marks a request so shared Ktor plumbing can identify its traffic bucket.
 *
 * @receiver Ktor request builder that is about to send a counted request.
 * @param category Traffic bucket associated with this request.
 *
 * @author Dmitriy Moroz
 */
fun HttpRequestBuilder.trackUsage(category: NetworkUsageCategory) {
    attributes.put(NetworkUsageCategoryAttribute, category)
}

/**
 * Serializes and records a JSON request body before sending it through Ktor.
 *
 * Counting the encoded payload here covers providers where Ktor later streams the body and the
 * original object is no longer available to the response parser.
 *
 * @receiver Ktor request builder that will receive serialized JSON content.
 * @param category Traffic bucket that should receive the encoded request byte count.
 * @param body Serializable request payload sent to the provider.
 * @param json Serializer used to encode the payload before it is installed as Ktor content.
 *
 * @author Dmitriy Moroz
 */
inline fun <reified T> HttpRequestBuilder.setTrackedJsonBody(
    category: NetworkUsageCategory,
    body: T,
    json: Json = defaultNetworkJson,
) {
    val payload = json.encodeToString(body)
    setTrackedTextBody(category, payload, ContentType.Application.Json)
}

/**
 * Records a text request body and installs it as Ktor content with the supplied content type.
 *
 * @receiver Ktor request builder that will receive text content.
 * @param category Traffic bucket that should receive the encoded request byte count.
 * @param body Text payload sent to the provider.
 * @param contentType Ktor content type used for the installed [TextContent].
 *
 * @author Dmitriy Moroz
 */
fun HttpRequestBuilder.setTrackedTextBody(
    category: NetworkUsageCategory,
    body: String,
    contentType: ContentType = ContentType.Application.Json,
) {
    trackUsage(category)
    NetworkUsageCounter.record(category, body.encodeToByteArray().size.toLong())
    contentType(contentType)
    setBody(TextContent(body, contentType))
}

/**
 * Records an externally measured byte delta for an existing Ktor traffic bucket.
 *
 * @param category Traffic bucket that should receive the externally measured bytes.
 * @param bytes Number of bytes measured outside the tracked request/response helpers.
 *
 * @author Dmitriy Moroz
 */
fun recordUsageBytes(category: NetworkUsageCategory, bytes: Long) {
    NetworkUsageCounter.record(category, bytes)
}

/**
 * Reads a response as JSON while counting the exact text bytes received from the server.
 *
 * @receiver Ktor response whose body should be counted before JSON decoding.
 * @param category Traffic bucket that should receive the response byte count.
 * @param json Serializer used to decode the counted response text.
 *
 * @author Dmitriy Moroz
 */
suspend inline fun <reified T> HttpResponse.trackedJsonBody(
    category: NetworkUsageCategory,
    json: Json = defaultNetworkJson,
): T {
    val text = trackedBodyAsText(category)
    return json.decodeFromString(text)
}

/**
 * Reads a response as text and records its UTF-8 byte size.
 *
 * @receiver Ktor response whose text body should be counted.
 * @param category Traffic bucket that should receive the response byte count.
 *
 * @author Dmitriy Moroz
 */
suspend fun HttpResponse.trackedBodyAsText(
    category: NetworkUsageCategory,
): String {
    val text = bodyAsText()
    NetworkUsageCounter.record(category, text.encodeToByteArray().size.toLong())
    return text
}

/**
 * Reads a binary response and records the number of bytes returned by Ktor.
 *
 * @receiver Ktor response whose binary body should be counted.
 * @param category Traffic bucket that should receive the response byte count.
 *
 * @author Dmitriy Moroz
 */
suspend fun HttpResponse.trackedByteArrayBody(
    category: NetworkUsageCategory,
): ByteArray {
    val bytes = body<ByteArray>()
    NetworkUsageCounter.record(category, bytes.size.toLong())
    return bytes
}

/**
 * Process-wide bridge from low-level network clients to the data layer.
 *
 * The data module installs [recorder] when its repository is created. Network code can then report
 * byte deltas without depending on Room, DI scopes, or presentation modules.
 *
 * @author Dmitriy Moroz
 */
object NetworkUsageCounter {
    /**
     * Callback owned by the data layer. Null means traffic accounting is unavailable in this build.
     *
     * @author Dmitriy Moroz
     */
    var recorder: ((NetworkUsageCategory, Long) -> Unit)? = null

    /**
     * Forwards positive byte deltas to the installed recorder.
     *
     * @param category Traffic bucket associated with the measured byte delta.
     * @param bytes Positive byte count to persist through the installed recorder.
     *
     * @author Dmitriy Moroz
     */
    fun record(category: NetworkUsageCategory, bytes: Long) {
        if (bytes > 0L) recorder?.invoke(category, bytes)
    }
}
