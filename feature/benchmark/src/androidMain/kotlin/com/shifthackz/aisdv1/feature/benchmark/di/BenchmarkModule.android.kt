package com.shifthackz.aisdv1.feature.benchmark.di

import com.shifthackz.aisdv1.feature.benchmark.AndroidBenchmarkDeviceProbe
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkDeviceProbe
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Creates Android benchmark hardware probe bindings.
 *
 * @return Android-specific Koin module.
 * @author Dmitriy Moroz
 */
internal actual fun platformBenchmarkModule(): Module = module {
    single<BenchmarkDeviceProbe> { AndroidBenchmarkDeviceProbe(androidContext()) }
}
