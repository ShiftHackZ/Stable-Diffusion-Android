package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.gateway.DatabaseClearGatewayImpl
import com.shifthackz.aisdv1.data.local.GenerationResultLocalDataSource
import com.shifthackz.aisdv1.data.local.ServerConfigurationLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionModelsLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionSamplersLocalDataSource
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localDataSourceModule = module {

    single {
        ServerUrlProvider { endpoint ->
            Single
                .fromCallable(get<PreferenceManager>()::serverUrl)
                .map { baseUrl -> "$baseUrl/$endpoint" }
        }
    }

    singleOf(::DatabaseClearGatewayImpl) bind DatabaseClearGateway::class

    factoryOf(::StableDiffusionModelsLocalDataSource) bind StableDiffusionModelsDataSource.Local::class
    factoryOf(::StableDiffusionSamplersLocalDataSource) bind StableDiffusionSamplersDataSource.Local::class
    factoryOf(::ServerConfigurationLocalDataSource) bind ServerConfigurationDataSource.Local::class
    factoryOf(::GenerationResultLocalDataSource) bind GenerationResultDataSource.Local::class
}
