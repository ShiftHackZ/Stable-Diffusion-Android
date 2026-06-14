package com.shifthackz.aisdv1.feature.benchmark.di

import com.shifthackz.aisdv1.feature.benchmark.BenchmarkManager
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkRepository
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkScoreEngine
import com.shifthackz.aisdv1.feature.benchmark.LocalGenerationBenchmarkGate
import com.shifthackz.aisdv1.feature.benchmark.RoomBenchmarkRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Exposes benchmark feature dependencies.
 *
 * @author Dmitriy Moroz
 */
val benchmarkModule: Module = module {
    includes(platformBenchmarkModule())
    single<BenchmarkRepository> { RoomBenchmarkRepository(dao = get()) }
    single { BenchmarkScoreEngine(timeProvider = get()) }
    single {
        BenchmarkManager(
            deviceProbe = get(),
            scoreEngine = get(),
            repository = get(),
            preferenceManager = get(),
        )
    }
    single {
        LocalGenerationBenchmarkGate(
            preferenceManager = get(),
            repository = get(),
        )
    }
}

/**
 * Creates platform-specific benchmark bindings.
 *
 * @return platform Koin module.
 * @author Dmitriy Moroz
 */
internal expect fun platformBenchmarkModule(): Module
