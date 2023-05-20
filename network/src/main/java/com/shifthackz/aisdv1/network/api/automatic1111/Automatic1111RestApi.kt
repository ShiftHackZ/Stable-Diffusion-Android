package com.shifthackz.aisdv1.network.api.automatic1111

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
import retrofit2.http.Url

interface Automatic1111RestApi {

    @GET
    fun healthCheck(@Url url: String): Completable

    @GET
    fun fetchConfiguration(@Url url: String): Single<ServerConfigurationRaw>

    @POST
    fun updateConfiguration(
        @Url url: String,
        @Body request: ServerConfigurationRaw,
    ): Completable

    @GET
    fun fetchSdModels(@Url url: String): Single<List<StableDiffusionModelRaw>>

    @GET
    fun fetchSamplers(@Url url: String): Single<List<StableDiffusionSamplerRaw>>

    @POST
    fun textToImage(
        @Url url: String,
        @Body request: TextToImageRequest,
    ): Single<SdGenerationResponse>

    @POST
    fun imageToImage(
        @Url url: String,
        @Body request: ImageToImageRequest
    ): Single<SdGenerationResponse>

    companion object {
        const val PATH_SD_OPTIONS = "sdapi/v1/options"
        const val PATH_SD_MODELS = "sdapi/v1/sd-models"
        const val PATH_SAMPLERS = "sdapi/v1/samplers"
        const val PATH_TXT_TO_IMG = "sdapi/v1/txt2img"
        const val PATH_IMG_TO_IMG = "sdapi/v1/img2img"
    }
}
