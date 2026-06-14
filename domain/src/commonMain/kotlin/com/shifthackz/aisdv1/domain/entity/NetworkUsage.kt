package com.shifthackz.aisdv1.domain.entity

/**
 * Aggregated network usage counters shown in Settings.
 *
 * @property modelDownloadBytes Bytes received while downloading local AI model files.
 * @property configBytes Bytes transferred by provider configuration and model-list sync calls.
 * @property inferenceBytes Bytes transferred by generation and inference API requests.
 * @param modelDownloadBytes Bytes received while downloading local AI model files.
 * @param configBytes Bytes transferred by provider configuration and model-list sync calls.
 * @param inferenceBytes Bytes transferred by generation and inference API requests.
 *
 * @author Dmitriy Moroz
 */
data class NetworkUsage(
    val modelDownloadBytes: Long = 0L,
    val configBytes: Long = 0L,
    val inferenceBytes: Long = 0L,
) {
    /**
     * Combined byte count used by Settings summaries and the donut chart center value.
     *
     * @author Dmitriy Moroz
     */
    val totalBytes: Long
        get() = modelDownloadBytes + configBytes + inferenceBytes
}

/**
 * Network usage buckets persisted by the app.
 *
 * The string [key] is the stable Room identifier. Keep it backward compatible because changing it
 * would orphan existing counters after an app update.
 *
 * @param key Stable Room identifier stored in the network usage table.
 *
 * @author Dmitriy Moroz
 */
enum class NetworkUsageBucket(val key: String) {
    MODEL_DOWNLOADS("model_downloads"),
    CONFIGS("configs"),
    INFERENCE("inference"),
    ;

    companion object {
        /**
         * Resolves a persisted bucket key, returning null for rows from unknown future versions.
         *
         * @param key Raw bucket key read from persistent storage.
         *
         * @author Dmitriy Moroz
         */
        fun parse(key: String): NetworkUsageBucket? = entries.firstOrNull { it.key == key }
    }
}
