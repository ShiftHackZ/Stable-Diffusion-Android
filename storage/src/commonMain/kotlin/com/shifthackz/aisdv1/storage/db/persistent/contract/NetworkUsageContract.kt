package com.shifthackz.aisdv1.storage.db.persistent.contract

/**
 * Room table contract for persisted network usage counters.
 *
 * Each row stores an accumulated byte count for one stable domain bucket key.
 *
 * @author Dmitriy Moroz
 */
object NetworkUsageContract {
    /**
     * Persistent table containing one row per traffic bucket.
     *
     * @author Dmitriy Moroz
     */
    const val TABLE = "network_usage"

    /**
     * Stable string key of a network usage bucket.
     *
     * @author Dmitriy Moroz
     */
    const val CATEGORY = "category"

    /**
     * Accumulated byte count since the last user reset.
     *
     * @author Dmitriy Moroz
     */
    const val BYTES = "bytes"
}
