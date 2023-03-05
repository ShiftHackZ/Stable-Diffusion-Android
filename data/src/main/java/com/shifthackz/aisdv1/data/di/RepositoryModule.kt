package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.repository.*
import com.shifthackz.aisdv1.domain.repository.*
import org.koin.dsl.module

val repositoryModule = module {

    factory<StableDiffusionTextToImageRepository> {
        StableDiffusionTextToImageRepositoryImpl(get(), get())
    }

    factory<StableDiffusionModelsRepository> {
        StableDiffusionModelsRepositoryImpl(get(), get())
    }

    factory<StableDiffusionSamplersRepository> {
        StableDiffusionSamplersRepositoryImpl(get(), get())
    }

    factory<ServerConfigurationRepository> {
        ServerConfigurationRepositoryImpl(get(), get())
    }

    factory<GenerationResultRepository> {
        GenerationResultRepositoryImpl(get())
    }
}
