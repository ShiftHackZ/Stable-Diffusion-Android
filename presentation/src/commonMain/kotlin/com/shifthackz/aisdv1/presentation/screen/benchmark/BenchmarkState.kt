package com.shifthackz.aisdv1.presentation.screen.benchmark

import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkDeviceInfo
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkResult

/**
 * Holds benchmark screen state.
 *
 * @property loadingDevice true while the screen captures hardware metadata.
 * @property loadingResult true while the latest stored benchmark result is being loaded.
 * @property running true while a synthetic benchmark is executing.
 * @property deviceInfo latest best-effort hardware snapshot.
 * @property latestResult most recent stored benchmark result.
 * @property error current user-facing failure message.
 * @author Dmitriy Moroz
 */
data class BenchmarkState(
    val loadingDevice: Boolean = true,
    val loadingResult: Boolean = true,
    val running: Boolean = false,
    val deviceInfo: BenchmarkDeviceInfo? = null,
    val latestResult: BenchmarkResult? = null,
    val error: String? = null,
) : MviState
