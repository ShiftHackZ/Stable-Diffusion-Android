package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.repository.AppVersionRepositoryImpl
import com.shifthackz.aisdv1.data.repository.CoinRepositoryImpl
import com.shifthackz.aisdv1.data.repository.FeatureFlagsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.GenerationResultRepositoryImpl
import com.shifthackz.aisdv1.data.repository.HordeGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.MotdRepositoryImpl
import com.shifthackz.aisdv1.data.repository.ServerConfigurationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionModelsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionSamplersRepositoryImpl
import com.shifthackz.aisdv1.domain.repository.AppVersionRepository
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import com.shifthackz.aisdv1.domain.repository.FeatureFlagsRepository
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.MotdRepository
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
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
}
