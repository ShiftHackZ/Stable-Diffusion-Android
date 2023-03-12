package com.shifthackz.aisdv1.network.api

import com.shifthackz.aisdv1.network.response.AppVersionResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface StableDiffusionAppUpdateRestApi {

    @GET("/version.json")
    fun fetchAppVersion(): Single<AppVersionResponse>
}
