package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.repository.ServerConfigurationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionModelsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionSamplersRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionTextToImageRepositoryImpl
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
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
}
