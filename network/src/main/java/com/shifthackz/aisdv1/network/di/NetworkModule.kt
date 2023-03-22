package com.shifthackz.aisdv1.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.network.api.StableDiffusionAppUpdateRestApi
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import com.shifthackz.aisdv1.network.extensions.withBaseUrl
import com.shifthackz.aisdv1.network.qualifiers.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val HTTP_TAG = "HTTP"
private const val HTTP_TIMEOUT = 10L

val networkModule = module {

    single<Gson> { GsonBuilder().create() }

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

    single<List<HttpInterceptor>> {
        buildList {

        }
    }

    single<List<NetworkInterceptor>> {
        buildList {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                debugLog(HTTP_TAG, message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            add(NetworkInterceptor(loggingInterceptor))
        }
    }

    single {
        OkHttpClient
            .Builder()
            .apply {
                get<List<HttpInterceptor>>().forEach(::addInterceptor)
                get<List<NetworkInterceptor>>().forEach(::addNetworkInterceptor)
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
            .create(StableDiffusionWebUiAutomaticRestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppUpdateApiUrl)
            .create(StableDiffusionAppUpdateRestApi::class.java)
    }

    single { ConnectivityMonitor() }
}
