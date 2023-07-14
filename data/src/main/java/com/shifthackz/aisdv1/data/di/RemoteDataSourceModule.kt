package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.core.common.extensions.fixUrlSlashes
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.data.gateway.ServerConnectivityGatewayImpl
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.data.remote.AppVersionRemoteDataSource
import com.shifthackz.aisdv1.data.remote.CoinRemoteDateSource
import com.shifthackz.aisdv1.data.remote.HordeGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.HordeStatusSource
import com.shifthackz.aisdv1.data.remote.MotdRemoteDataSource
import com.shifthackz.aisdv1.data.remote.ServerConfigurationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionGenerationRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionModelsRemoteDataSource
import com.shifthackz.aisdv1.data.remote.StableDiffusionSamplersRemoteDataSource
import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.domain.datasource.CoinDataSource
import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.MotdDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
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
            val links = get<LinksProvider>()
            val chain = if (prefs.useSdAiCloud) Single.fromCallable(links::cloudUrl)
            else Single.fromCallable(prefs::serverUrl)

            chain
                .map(String::fixUrlSlashes)
                .map { baseUrl -> "$baseUrl/$endpoint" }
        }
    }
    singleOf(::HordeStatusSource) bind HordeGenerationDataSource.StatusSource::class
    factoryOf(::HordeGenerationRemoteDataSource) bind HordeGenerationDataSource.Remote::class
    factoryOf(::StableDiffusionGenerationRemoteDataSource) bind StableDiffusionGenerationDataSource.Remote::class
    factoryOf(::StableDiffusionSamplersRemoteDataSource) bind StableDiffusionSamplersDataSource.Remote::class
    factoryOf(::StableDiffusionModelsRemoteDataSource) bind StableDiffusionModelsDataSource.Remote::class
    factoryOf(::ServerConfigurationRemoteDataSource) bind ServerConfigurationDataSource.Remote::class
    factoryOf(::AppVersionRemoteDataSource) bind AppVersionDataSource.Remote::class
    factoryOf(::CoinRemoteDateSource) bind CoinDataSource.Remote::class
    factoryOf(::MotdRemoteDataSource) bind MotdDataSource.Remote::class

    factory<ServerConnectivityGateway> {
        val lambda: () -> Boolean = {
            val prefs = get<PreferenceManager>()
            prefs.source == ServerSource.CUSTOM
        }
        val monitor = get<ConnectivityMonitor> { parametersOf(lambda) }
        ServerConnectivityGatewayImpl(monitor, get())
    }
}
