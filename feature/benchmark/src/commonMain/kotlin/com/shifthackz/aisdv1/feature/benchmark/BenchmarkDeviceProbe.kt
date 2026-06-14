package com.shifthackz.aisdv1.feature.benchmark

/**
 * Collects device information without loading AI model runtimes.
 *
 * @author Dmitriy Moroz
 */
interface BenchmarkDeviceProbe {
    /**
     * Captures a best-effort hardware snapshot.
     *
     * Implementations must catch platform API failures internally and return
     * conservative fallback values instead of throwing.
     *
     * @return hardware snapshot for the current device.
     * @author Dmitriy Moroz
     */
    suspend fun capture(): BenchmarkDeviceInfo
}
