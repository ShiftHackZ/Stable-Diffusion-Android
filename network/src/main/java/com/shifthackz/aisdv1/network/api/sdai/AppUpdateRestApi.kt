package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.response.AppVersionResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface AppUpdateRestApi {

    @GET("/version.json")
    fun fetchAppVersion(): Single<AppVersionResponse>
}
