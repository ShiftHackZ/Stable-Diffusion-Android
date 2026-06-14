package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.entity.NetworkUsageBucket
import com.shifthackz.aisdv1.network.client.NetworkUsageCategory
import com.shifthackz.aisdv1.storage.db.persistent.entity.NetworkUsageEntity

/**
 * Converts persisted network usage rows into a complete domain snapshot.
 *
 * Missing rows are treated as zero so the UI can render all traffic buckets even after a fresh
 * install or after the user resets statistics from the network usage screen.
 *
 * @receiver Room rows stored for known or future network usage buckets.
 *
 * @author Dmitriy Moroz
 */
internal fun List<NetworkUsageEntity>.toDomain(): NetworkUsage {
    var modelDownloadBytes = 0L
    var configBytes = 0L
    var inferenceBytes = 0L
    forEach { entity ->
        when (NetworkUsageBucket.parse(entity.category)) {
            NetworkUsageBucket.MODEL_DOWNLOADS -> modelDownloadBytes = entity.bytes
            NetworkUsageBucket.CONFIGS -> configBytes = entity.bytes
            NetworkUsageBucket.INFERENCE -> inferenceBytes = entity.bytes
            null -> Unit
        }
    }
    return NetworkUsage(
        modelDownloadBytes = modelDownloadBytes.coerceAtLeast(0L),
        configBytes = configBytes.coerceAtLeast(0L),
        inferenceBytes = inferenceBytes.coerceAtLeast(0L),
    )
}

/**
 * Maps low-level Ktor request categories to Room-backed domain buckets.
 *
 * Model downloads are counted outside Ktor because native platform downloaders stream those files
 * directly and report byte progress through the repository instead.
 *
 * @receiver Ktor-level category attached to a request or response body helper.
 *
 * @author Dmitriy Moroz
 */
internal fun NetworkUsageCategory.toDomain(): NetworkUsageBucket = when (this) {
    NetworkUsageCategory.CONFIGS -> NetworkUsageBucket.CONFIGS
    NetworkUsageCategory.INFERENCE -> NetworkUsageBucket.INFERENCE
}
