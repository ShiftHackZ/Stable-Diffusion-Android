package com.shifthackz.aisdv1.network.api

import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionModelRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionSamplerRaw
import com.shifthackz.aisdv1.network.request.ImageToImageRequest
import com.shifthackz.aisdv1.network.request.TextToImageRequest
import com.shifthackz.aisdv1.network.response.SdGenerationResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StableDiffusionWebUiAutomaticRestApi {

    @GET(PATH_HEALTH_CHECK)
    fun healthCheck(): Completable

    @GET(PATH_SD_OPTIONS)
    fun fetchConfiguration(): Single<ServerConfigurationRaw>

    @POST(PATH_SD_OPTIONS)
    fun updateConfiguration(
        @Body request: ServerConfigurationRaw,
    ): Completable

    @GET(PATH_SD_MODELS)
    fun fetchSdModels(): Single<List<StableDiffusionModelRaw>>

    @GET(PATH_SAMPLERS)
    fun fetchSamplers(): Single<List<StableDiffusionSamplerRaw>>

    @POST(PATH_TXT_TO_IMG)
    fun textToImage(
        @Body request: TextToImageRequest,
    ): Single<SdGenerationResponse>

    @POST(PATH_IMG_TO_IMG)
    fun imageToImage(
        @Body request: ImageToImageRequest
    ): Single<SdGenerationResponse>

    companion object {
        private const val PATH_HEALTH_CHECK = "/"
        private const val PATH_SD_OPTIONS = "/sdapi/v1/options"
        private const val PATH_SD_MODELS = "/sdapi/v1/sd-models"
        private const val PATH_SAMPLERS = "/sdapi/v1/samplers"
        private const val PATH_TXT_TO_IMG = "/sdapi/v1/txt2img"
        private const val PATH_IMG_TO_IMG = "/sdapi/v1/img2img"
    }
}
