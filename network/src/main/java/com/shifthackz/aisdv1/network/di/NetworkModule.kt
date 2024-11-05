package com.shifthackz.aisdv1.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.horde.HordeRestApi
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceApi
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceInferenceApi
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceInferenceApiImpl
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnRestApi
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnRestApiImpl
import com.shifthackz.aisdv1.network.api.openai.OpenAiApi
import com.shifthackz.aisdv1.network.api.sdai.DonateApi
import com.shifthackz.aisdv1.network.api.sdai.DownloadableModelsApi
import com.shifthackz.aisdv1.network.api.sdai.DownloadableModelsApiImpl
import com.shifthackz.aisdv1.network.api.sdai.HuggingFaceModelsApi
import com.shifthackz.aisdv1.network.api.sdai.ReportApi
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiApiImpl
import com.shifthackz.aisdv1.network.authenticator.RestAuthenticator
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import com.shifthackz.aisdv1.network.error.StabilityAiErrorMapper
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val HTTP_TIMEOUT = 10L

val networkModule = module {

    single<Gson> {
        GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()
    }

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
                NetworkInterceptor(LoggingInterceptor().get()),
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
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAutomaticApiUrl)
            .create(SwarmUiApi.RawApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().hordeApiUrl)
            .create(HordeRestApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(DownloadableModelsApi.RawApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(HuggingFaceModelsApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionAppApiUrl)
            .create(DonateApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stableDiffusionReportApiUrl)
            .create(ReportApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().imageCdnApiUrl)
            .create(ImageCdnRestApi.RawApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().huggingFaceInferenceApiUrl)
            .create(HuggingFaceInferenceApi.RawApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().huggingFaceApiUrl)
            .create(HuggingFaceApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().openAiApiUrl)
            .create(OpenAiApi::class.java)
    }

    single {
        get<Retrofit.Builder>()
            .withBaseUrl(get<ApiUrlProvider>().stabilityAiApiUrl)
            .create(StabilityAiApi::class.java)
    }

    singleOf(::ImageCdnRestApiImpl) bind ImageCdnRestApi::class
    singleOf(::DownloadableModelsApiImpl) bind DownloadableModelsApi::class
    singleOf(::HuggingFaceInferenceApiImpl) bind HuggingFaceInferenceApi::class
    singleOf(::SwarmUiApiImpl) bind SwarmUiApi::class

    factory { params ->
        ConnectivityMonitor(params.get())
    }

    factory { StabilityAiErrorMapper(get()) }
}
