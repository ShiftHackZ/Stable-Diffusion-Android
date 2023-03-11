package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.remote.ServerConfigurationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionModelsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionSamplersRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionTextToImageRemoteDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionTextToImageDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteDataSourceModule = module {
    factoryOf(::StableDiffusionTextToImageRemoteDataSource) bind StableDiffusionTextToImageDataSource.Remote::class
    factoryOf(::StableDiffusionSamplersRemoteDataSource) bind StableDiffusionSamplersDataSource.Remote::class
    factoryOf(::StableDiffusionModelsRemoteDataSource) bind StableDiffusionModelsDataSource.Remote::class
    factoryOf(::ServerConfigurationRemoteDataSource) bind ServerConfigurationDataSource.Remote::class
}
