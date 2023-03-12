package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.gateway.ServerConnectivityGatewayImpl
import com.shifthackz.aisdv1.data.remote.*
import com.shifthackz.aisdv1.domain.datasource.*
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteDataSourceModule = module {
    factoryOf(::ServerConnectivityGatewayImpl) bind ServerConnectivityGateway::class
    factoryOf(::StableDiffusionGenerationRemoteDataSource) bind StableDiffusionGenerationDataSource.Remote::class
    factoryOf(::StableDiffusionSamplersRemoteDataSource) bind StableDiffusionSamplersDataSource.Remote::class
    factoryOf(::StableDiffusionModelsRemoteDataSource) bind StableDiffusionModelsDataSource.Remote::class
    factoryOf(::ServerConfigurationRemoteDataSource) bind ServerConfigurationDataSource.Remote::class
    factoryOf(::AppVersionRemoteDataSource) bind AppVersionDataSource.Remote::class
}
