package com.shifthackz.aisdv1.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.horde.HordeRestApi
import com.shifthackz.aisdv1.network.api.sdai.AppUpdateRestApi
import com.shifthackz.aisdv1.network.api.sdai.CoinsRestApi
import com.shifthackz.aisdv1.network.api.sdai.FeatureFlagsRestApi
import com.shifthackz.aisdv1.network.api.sdai.MotdRestApi
import com.shifthackz.aisdv1.network.authenticator.RestAuthenticator
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import com.shifthackz.aisdv1.network.extensions.withBaseUrl
import com.shifthackz.aisdv1.network.interceptor.HeaderInterceptor
import com.shifthackz.aisdv1.network.interceptor.LoggingInterceptor
import com.shifthackz.aisdv1.network.qualifiers.ApiUrlProvider
import com.shifthackz.aisdv1.network.qualifiers.HttpInterceptor
import com.shifthackz.aisdv1.network.qualifiers.HttpInterceptors
import com.shifthackz.aisdv1.network.qualifiers.NetworkInterceptor
import com.shifthackz.aisdv1.network.qualifiers.NetworkInterceptors
import com.shifthackz.aisdv1.network.qualifiers.RetrofitCallAdapters
import com.shifthackz.aisdv1.network.qualifiers.RetrofitConverterFactories
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val HTTP_TIMEOUT = 10L

val networkModule = module {

    single<Gson> { GsonBuilder().create() }

    single { RestAuthenticator(get()) }

    single {
        RetrofitConverterFactories(
            buildList {
                add(GsonConverterFactory.create(get()))
            }
        )
    }

    single {
        RetrofitCallAdapters(
            buildList {
                add(RxJava3CallAdapterFactory.create())
            }
        )
    }

    single {
        HttpInterceptors(
            listOf(
                HttpInterceptor(HeaderInterceptor(get(), get())),
            )
        )
    }

    single {
        NetworkInterceptors(
            listOf(
                NetworkInterceptor(LoggingInterceptor(get()).get()),
            )
        )
    }

    single {
        OkHttpClient
            .Builder()
            .apply {
                get<HttpInterceptors>().interceptors.forEach(::addInterceptor)
                get<NetworkInterceptors>().interceptors.forEach(::addNetworkInterceptor)
                authenticator(get<RestAuthenticator>())
            }
            .connectTimeout(HTTP_TIMEOUT, TimeUnit.MINUTES)
            .readTimeout(HTTP_TIMEOUT, TimeUnit.MINUTES)
            .build()
    }

    single<Retrofit.Builder> {
        Retrofit
            .Builder()
            .apply {
                get<RetrofitConverterFactories>().data.forEach(::addConverterFactory)
                get<RetrofitCallAdapters>().data.forEach(::addCallAdapterFactory)
            }
            .client(get())
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAutomaticApiUrl)
            .create(Automatic1111RestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(AppUpdateRestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(CoinsRestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(MotdRestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(FeatureFlagsRestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().hordeApiUrl)
            .create(HordeRestApi::class.java)
    }

    factory {params ->
        ConnectivityMonitor(params.get())
    }
}
