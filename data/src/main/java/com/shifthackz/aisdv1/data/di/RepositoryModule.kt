package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.repository.*
import com.shifthackz.aisdv1.domain.repository.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    factoryOf(::StableDiffusionGenerationRepositoryImpl) bind StableDiffusionGenerationRepository::class
    factoryOf(::StableDiffusionModelsRepositoryImpl) bind StableDiffusionModelsRepository::class
    factoryOf(::StableDiffusionSamplersRepositoryImpl) bind StableDiffusionSamplersRepository::class
    factoryOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
    factoryOf(::GenerationResultRepositoryImpl) bind GenerationResultRepository::class
}
