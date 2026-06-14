package com.shifthackz.aisdv1.feature.benchmark.di

import com.shifthackz.aisdv1.feature.benchmark.BenchmarkDeviceProbe
import com.shifthackz.aisdv1.feature.benchmark.IosBenchmarkDeviceProbe
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Creates iOS benchmark hardware probe bindings.
 *
 * @return iOS-specific Koin module.
 * @author Dmitriy Moroz
 */
internal actual fun platformBenchmarkModule(): Module = module {
    single<BenchmarkDeviceProbe> { IosBenchmarkDeviceProbe() }
}
