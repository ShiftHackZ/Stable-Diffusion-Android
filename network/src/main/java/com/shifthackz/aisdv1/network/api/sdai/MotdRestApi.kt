package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.response.MotdResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface MotdRestApi {

    @GET("/motd.json")
    fun fetchMotd(): Single<MotdResponse>
}
