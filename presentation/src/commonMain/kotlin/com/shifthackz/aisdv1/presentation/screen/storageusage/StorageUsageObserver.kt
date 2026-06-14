package com.shifthackz.aisdv1.presentation.screen.storageusage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Shared invalidation source for storage usage snapshots.
 *
 * Storage size comes from several non-Room sources, including cache directories and downloaded
 * model files. Those sources do not expose flows, so writers notify this observer after mutations
 * and both Settings and the standalone storage screen reload a fresh snapshot from the same stream.
 *
 * @author Dmitriy Moroz
 */
class StorageUsageObserver {

    private val revision = MutableStateFlow(0L)

    /**
     * Emits a monotonically increasing revision whenever storage usage should be reloaded.
     *
     * @author Dmitriy Moroz
     */
    fun observe(): Flow<Long> = revision

    /**
     * Invalidates the current snapshot after cache, gallery, or model files change.
     *
     * @author Dmitriy Moroz
     */
    fun notifyChanged() {
        revision.update { value -> value + 1L }
    }
}
