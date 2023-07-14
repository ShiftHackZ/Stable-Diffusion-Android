package com.shifthackz.aisdv1.network.api.horde

import com.shifthackz.aisdv1.network.request.HordeGenerationAsyncRequest
import com.shifthackz.aisdv1.network.response.HordeGenerationAsyncResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckFullResponse
import com.shifthackz.aisdv1.network.response.HordeGenerationCheckResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HordeRestApi {

    @POST("/api/v2/generate/async")
    fun generateAsync(@Body request: HordeGenerationAsyncRequest): Single<HordeGenerationAsyncResponse>

    @GET("/api/v2/generate/check/{id}")
    fun checkGeneration(@Path("id") id: String): Single<HordeGenerationCheckResponse>

    @GET("/api/v2/generate/status/{id}")
    fun checkStatus(@Path("id") id: String): Single<HordeGenerationCheckFullResponse>
}
