package com.shifthackz.aisdv1.demo.serialize

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Coordinates `DemoDataSerializer` behavior in the SDAI demo layer.
 *
 * @author Dmitriy Moroz
 */
internal class DemoDataSerializer {

    /**
     * Exposes the `mutex` value used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    private val mutex = Mutex()
    /**
     * Exposes the `availableIndexes` value used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    private var availableIndexes = shuffledIndexes()
    /**
     * Exposes the `previousIndex` value used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    private var previousIndex: Int? = null

    /**
     * Loads SDAI data through `readDemoAssets`.
     *
     * @author Dmitriy Moroz
     */
    fun readDemoAssets() = DemoAssets.images

    /**
     * Executes the `nextDemoAsset` step in the SDAI demo layer.
     *
     * @return Result produced by `nextDemoAsset`.
     * @author Dmitriy Moroz
     */
    suspend fun nextDemoAsset(): String = mutex.withLock {
        if (availableIndexes.isEmpty()) {
            availableIndexes = shuffledIndexes()
        }
        availableIndexes.removeAt(availableIndexes.lastIndex)
            .also { previousIndex = it }
            .let(DemoAssets.images::get)
    }

    /**
     * Executes the `shuffledIndexes` step in the SDAI demo layer.
     *
     * @return Result produced by `shuffledIndexes`.
     * @author Dmitriy Moroz
     */
    private fun shuffledIndexes(): MutableList<Int> =
        DemoAssets.images.indices.shuffled().toMutableList().apply {
            val previous = previousIndex ?: return@apply
            if (size <= 1 || last() != previous) return@apply
            val swapIndex = indexOfFirst { it != previous }
            this[lastIndex] = this[swapIndex]
            this[swapIndex] = previous
        }
}
