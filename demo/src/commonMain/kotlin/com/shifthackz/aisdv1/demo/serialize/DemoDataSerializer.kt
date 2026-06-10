package com.shifthackz.aisdv1.demo.serialize

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DemoDataSerializer {

    private val mutex = Mutex()
    private var availableIndexes = shuffledIndexes()
    private var previousIndex: Int? = null

    fun readDemoAssets() = DemoAssets.images

    suspend fun nextDemoAsset(): String = mutex.withLock {
        if (availableIndexes.isEmpty()) {
            availableIndexes = shuffledIndexes()
        }
        availableIndexes.removeAt(availableIndexes.lastIndex)
            .also { previousIndex = it }
            .let(DemoAssets.images::get)
    }

    private fun shuffledIndexes(): MutableList<Int> =
        DemoAssets.images.indices.shuffled().toMutableList().apply {
            val previous = previousIndex ?: return@apply
            if (size <= 1 || last() != previous) return@apply
            val swapIndex = indexOfFirst { it != previous }
            this[lastIndex] = this[swapIndex]
            this[swapIndex] = previous
        }
}
