package com.shifthackz.aisdv1.presentation.model

/**
 * Maps raw storage counters into byte values that should be visible in usage UI.
 *
 * Filesystem counters stay honest at the platform boundary, while UI can hide platform bookkeeping
 * noise such as tiny empty-directory sizes that do not represent user-created data.
 *
 * @author Dmitriy Moroz
 */
interface StorageUsageByteMapper {
    /**
     * Converts a raw storage counter into a user-visible amount.
     *
     * @param bytes Raw byte count returned by a platform filesystem or domain source.
     * @return Byte count that should be shown and aggregated by presentation state.
     *
     * @author Dmitriy Moroz
     */
    fun mapStorageBytesForUi(bytes: Long): Long = bytes.coerceAtLeast(0L)
}
