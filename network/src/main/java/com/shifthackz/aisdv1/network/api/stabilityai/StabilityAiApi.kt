package com.shifthackz.aisdv1.network.api.stabilityai

import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw
import com.shifthackz.aisdv1.network.request.StabilityImageToImageRequest
import com.shifthackz.aisdv1.network.request.StabilityTextToImageRequest
import com.shifthackz.aisdv1.network.response.StabilityCreditsResponse
import com.shifthackz.aisdv1.network.response.StabilityGenerationResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StabilityAiApi {

    /**
     * Gets stability ai user info.
     *
     * Documentation: https://platform.stability.ai/docs/api-reference/#tag/v1user
     */
    @GET("/v1/user/account")
    fun validateBearerToken(): Completable

    /**
     * Gets stability ai credits amount.
     *
     * Documentation: https://platform.stability.ai/docs/api-reference#tag/v1user/operation/userBalance
     */
    @GET("/v1/user/balance")
    fun fetchCredits(): Single<StabilityCreditsResponse>

    /**
     * Gets a list of stability ai engines
     *
     * Documentation: https://platform.stability.ai/docs/api-reference/#tag/v1engines
     */
    @GET("/v1/engines/list")
    fun fetchEngines(): Single<List<StabilityAiEngineRaw>>

    /**
     * Generates text to image.
     *
     * Documentation: https://platform.stability.ai/docs/api-reference/#tag/v1generation/operation/textToImage
     */
    @POST("/v1/generation/{engine_id}/text-to-image")
    fun textToImage(
        @Path("engine_id") engineId: String,
        @Body request: StabilityTextToImageRequest,
    ): Single<StabilityGenerationResponse>

    /**
     * Generates image to image
     *
     * Documentation: https://platform.stability.ai/docs/api-reference/#tag/v1generation/operation/imageToImage
     */
    @POST("/v1/generation/{engine_id}/image-to-image")
    fun imageToImage(
        @Path("engine_id") engineId: String,
        @Body request: StabilityImageToImageRequest,
    ): Single<StabilityGenerationResponse>
}
