package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.remote.ServerConfigurationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionModelsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionSamplersRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionTextToImageRemoteDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import org.koin.dsl.module

val remoteDataSourceModule = module {

    factory<StableDiffusionTextToImageDataSource.Remote> {
        StableDiffusionTextToImageRemoteDataSource(get())
    }

    factory<StableDiffusionSamplersDataSource.Remote> {
        StableDiffusionSamplersRemoteDataSource(get())
    }

    factory<StableDiffusionModelsDataSource.Remote> {
        StableDiffusionModelsRemoteDataSource(get())
    }

    factory<ServerConfigurationDataSource.Remote> {
        ServerConfigurationRemoteDataSource(get())
    }
}
