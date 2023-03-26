package com.shifthackz.aisdv1.network.api

import com.shifthackz.aisdv1.network.response.CoinsResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface StableDiffusionCoinsRestApi {

    @GET("/coins.json")
    fun fetchCoinsConfig(): Single<CoinsResponse>
}
