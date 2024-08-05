package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.model.SupporterRaw
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface DonateApi {

    @GET("/supporters.json")
    fun fetchSupporters(): Single<List<SupporterRaw>>
}
