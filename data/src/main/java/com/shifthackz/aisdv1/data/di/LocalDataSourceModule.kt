package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.gateway.DatabaseClearGatewayImpl
import com.shifthackz.aisdv1.data.gateway.mediastore.MediaStoreGatewayFactory
import com.shifthackz.aisdv1.data.local.DownloadableModelLocalDataSource
import com.shifthackz.aisdv1.data.local.EmbeddingsLocalDataSource
import com.shifthackz.aisdv1.data.local.GenerationResultLocalDataSource
import com.shifthackz.aisdv1.data.local.HuggingFaceModelsLocalDataSource
import com.shifthackz.aisdv1.data.local.LorasLocalDataSource
import com.shifthackz.aisdv1.data.local.ServerConfigurationLocalDataSource
import com.shifthackz.aisdv1.data.local.StabilityAiCreditsLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionHyperNetworksLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionModelsLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionSamplersLocalDataSource
import com.shifthackz.aisdv1.data.local.SupportersLocalDataSource
import com.shifthackz.aisdv1.data.local.SwarmUiModelsLocalDataSource
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.gateway.DatabaseClearGateway
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localDataSourceModule = module {
    singleOf(::DatabaseClearGatewayImpl) bind DatabaseClearGateway::class
    // !!! Do not use [factoryOf] for StabilityAiCreditsLocalDataSource, it has default constructor
    single<StabilityAiCreditsDataSource.Local> { StabilityAiCreditsLocalDataSource() }
    factoryOf(::StableDiffusionModelsLocalDataSource) bind StableDiffusionModelsDataSource.Local::class
    factoryOf(::StableDiffusionSamplersLocalDataSource) bind StableDiffusionSamplersDataSource.Local::class
    factoryOf(::LorasLocalDataSource) bind LorasDataSource.Local::class
    factoryOf(::StableDiffusionHyperNetworksLocalDataSource) bind StableDiffusionHyperNetworksDataSource.Local::class
    factoryOf(::EmbeddingsLocalDataSource) bind EmbeddingsDataSource.Local::class
    factoryOf(::SwarmUiModelsLocalDataSource) bind SwarmUiModelsDataSource.Local::class
    factoryOf(::ServerConfigurationLocalDataSource) bind ServerConfigurationDataSource.Local::class
    factoryOf(::GenerationResultLocalDataSource) bind GenerationResultDataSource.Local::class
    factoryOf(::DownloadableModelLocalDataSource) bind DownloadableModelDataSource.Local::class
    factoryOf(::HuggingFaceModelsLocalDataSource) bind HuggingFaceModelsDataSource.Local::class
    factoryOf(::SupportersLocalDataSource) bind SupportersDataSource.Local::class
    factory { MediaStoreGatewayFactory(androidContext(), get()).invoke() }
}
