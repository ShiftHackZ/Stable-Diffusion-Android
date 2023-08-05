package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.repository.*
import com.shifthackz.aisdv1.domain.repository.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::TemporaryGenerationResultRepositoryImpl) bind TemporaryGenerationResultRepository::class
    factoryOf(::LocalDiffusionGenerationRepositoryImpl) bind LocalDiffusionGenerationRepository::class
    factoryOf(::HordeGenerationRepositoryImpl) bind HordeGenerationRepository::class
    factoryOf(::StableDiffusionGenerationRepositoryImpl) bind StableDiffusionGenerationRepository::class
    factoryOf(::StableDiffusionModelsRepositoryImpl) bind StableDiffusionModelsRepository::class
    factoryOf(::StableDiffusionSamplersRepositoryImpl) bind StableDiffusionSamplersRepository::class
    factoryOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
    factoryOf(::GenerationResultRepositoryImpl) bind GenerationResultRepository::class
    factoryOf(::AppVersionRepositoryImpl) bind AppVersionRepository::class
    factoryOf(::CoinRepositoryImpl) bind CoinRepository::class
    factoryOf(::MotdRepositoryImpl) bind MotdRepository::class
    factoryOf(::FeatureFlagsRepositoryImpl) bind FeatureFlagsRepository::class
    factoryOf(::RandomImageRepositoryImpl) bind RandomImageRepository::class
    factoryOf(::DownloadableModelRepositoryImpl) bind DownloadableModelRepository::class
}
