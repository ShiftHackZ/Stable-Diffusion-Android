package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.data.gateway.ServerConnectivityGatewayImpl
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.data.remote.*
import com.shifthackz.aisdv1.domain.datasource.*
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import io.reactivex.rxjava3.core.Single
import org.koin.core.module.dsl.factoryOf
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
            chain.map { baseUrl -> "$baseUrl/$endpoint" }
        }
    }

    factoryOf(::StableDiffusionGenerationRemoteDataSource) bind StableDiffusionGenerationDataSource.Remote::class
    factoryOf(::StableDiffusionSamplersRemoteDataSource) bind StableDiffusionSamplersDataSource.Remote::class
    factoryOf(::StableDiffusionModelsRemoteDataSource) bind StableDiffusionModelsDataSource.Remote::class
    factoryOf(::ServerConfigurationRemoteDataSource) bind ServerConfigurationDataSource.Remote::class
    factoryOf(::AppVersionRemoteDataSource) bind AppVersionDataSource.Remote::class
    factoryOf(::CoinRemoteDateSource) bind CoinDataSource.Remote::class

    factory<ServerConnectivityGateway> {
        val lambda: () -> Boolean = { get<PreferenceManager>().useSdAiCloud }
        val monitor = get<ConnectivityMonitor> { parametersOf(lambda) }
        ServerConnectivityGatewayImpl(monitor, get())
    }
}
