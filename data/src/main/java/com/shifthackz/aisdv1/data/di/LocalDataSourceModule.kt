package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.gateway.DatabaseClearGatewayImpl
import com.shifthackz.aisdv1.data.gateway.mediastore.MediaStoreGatewayFactory
import com.shifthackz.aisdv1.data.local.AppVersionLocalDataSource
import com.shifthackz.aisdv1.data.local.CoinLocalDataSource
import com.shifthackz.aisdv1.data.local.FeatureFlagsLocalDataSource
import com.shifthackz.aisdv1.data.local.GenerationResultLocalDataSource
import com.shifthackz.aisdv1.data.local.ServerConfigurationLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionModelsLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionSamplersLocalDataSource
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.domain.datasource.CoinDataSource
import com.shifthackz.aisdv1.domain.datasource.FeatureFlagsDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localDataSourceModule = module {
    singleOf(::DatabaseClearGatewayImpl) bind DatabaseClearGateway::class
    singleOf(::FeatureFlagsLocalDataSource) bind FeatureFlagsDataSource.Local::class
    factoryOf(::StableDiffusionModelsLocalDataSource) bind StableDiffusionModelsDataSource.Local::class
    factoryOf(::StableDiffusionSamplersLocalDataSource) bind StableDiffusionSamplersDataSource.Local::class
    factoryOf(::ServerConfigurationLocalDataSource) bind ServerConfigurationDataSource.Local::class
    factoryOf(::GenerationResultLocalDataSource) bind GenerationResultDataSource.Local::class
    factoryOf(::AppVersionLocalDataSource) bind AppVersionDataSource.Local::class
    factoryOf(::CoinLocalDataSource) bind CoinDataSource.Local::class
    factory { MediaStoreGatewayFactory(androidContext(), get()).invoke() }
}
