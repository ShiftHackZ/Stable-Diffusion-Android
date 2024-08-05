package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.data.gateway.ServerConnectivityGatewayImpl
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.data.remote.DownloadableModelRemoteDataSource
import com.shifthackz.aisdv1.data.remote.HordeGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.HordeStatusSource
import com.shifthackz.aisdv1.data.remote.HuggingFaceGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.HuggingFaceModelsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.OpenAiGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.RandomImageRemoteDataSource
import com.shifthackz.aisdv1.data.remote.ServerConfigurationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StabilityAiEnginesRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StabilityAiGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionEmbeddingsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionHyperNetworksRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionLorasRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionModelsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionSamplersRemoteDataSource
import com.shifthackz.aisdv1.data.remote.SupportersRemoteDataSource
import com.shifthackz.aisdv1.data.remote.SwarmUiEmbeddingsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.SwarmUiGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.SwarmUiLorasRemoteDataSource
import com.shifthackz.aisdv1.data.remote.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.SwarmUiSessionDataSourceImpl
import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import io.reactivex.rxjava3.core.Single
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single {
        ServerUrlProvider { endpoint ->
            val prefs = get<PreferenceManager>()
            val chain = if (prefs.source == ServerSource.SWARM_UI) {
                Single.fromCallable(prefs::swarmUiServerUrl)
            } else {
                Single.fromCallable(prefs::automatic1111ServerUrl)
            }
            chain
                .map(String::fixUrlSlashes)
                .map { baseUrl -> "$baseUrl/$endpoint" }
        }
    }
    singleOf(::HordeStatusSource) bind HordeGenerationDataSource.StatusSource::class
    factoryOf(::HordeGenerationRemoteDataSource) bind HordeGenerationDataSource.Remote::class
    factoryOf(::HuggingFaceGenerationRemoteDataSource) bind HuggingFaceGenerationDataSource.Remote::class
    factoryOf(::OpenAiGenerationRemoteDataSource) bind OpenAiGenerationDataSource.Remote::class
    factoryOf(::SwarmUiSessionDataSourceImpl) bind SwarmUiSessionDataSource::class
    factoryOf(::SwarmUiGenerationRemoteDataSource) bind SwarmUiGenerationDataSource.Remote::class
    factoryOf(::SwarmUiModelsRemoteDataSource) bind SwarmUiModelsDataSource.Remote::class
    factoryOf(::SwarmUiLorasRemoteDataSource) bind LorasDataSource.Remote.SwarmUi::class
    factoryOf(::SwarmUiEmbeddingsRemoteDataSource) bind EmbeddingsDataSource.Remote.SwarmUi::class
    factoryOf(::StableDiffusionGenerationRemoteDataSource) bind StableDiffusionGenerationDataSource.Remote::class
    factoryOf(::StableDiffusionSamplersRemoteDataSource) bind StableDiffusionSamplersDataSource.Remote::class
    factoryOf(::StableDiffusionModelsRemoteDataSource) bind StableDiffusionModelsDataSource.Remote::class
    factoryOf(::StableDiffusionLorasRemoteDataSource) bind LorasDataSource.Remote.Automatic1111::class
    factoryOf(::StableDiffusionHyperNetworksRemoteDataSource) bind StableDiffusionHyperNetworksDataSource.Remote::class
    factoryOf(::StableDiffusionEmbeddingsRemoteDataSource) bind EmbeddingsDataSource.Remote.Automatic1111::class
    factoryOf(::ServerConfigurationRemoteDataSource) bind ServerConfigurationDataSource.Remote::class
    factoryOf(::RandomImageRemoteDataSource) bind RandomImageDataSource.Remote::class
    factoryOf(::DownloadableModelRemoteDataSource) bind DownloadableModelDataSource.Remote::class
    factoryOf(::SupportersRemoteDataSource) bind SupportersDataSource.Remote::class
    factoryOf(::HuggingFaceModelsRemoteDataSource) bind HuggingFaceModelsDataSource.Remote::class
    factoryOf(::StabilityAiGenerationRemoteDataSource) bind StabilityAiGenerationDataSource.Remote::class
    factoryOf(::StabilityAiCreditsRemoteDataSource) bind StabilityAiCreditsDataSource.Remote::class
    factoryOf(::StabilityAiEnginesRemoteDataSource) bind StabilityAiEnginesDataSource.Remote::class

    factory<ServerConnectivityGateway> {
        val lambda: () -> Boolean = {
            val prefs = get<PreferenceManager>()
            prefs.source == ServerSource.AUTOMATIC1111 || prefs.source == ServerSource.SWARM_UI
        }
        val monitor = get<ConnectivityMonitor> { parametersOf(lambda) }
        ServerConnectivityGatewayImpl(monitor, get())
    }
}
