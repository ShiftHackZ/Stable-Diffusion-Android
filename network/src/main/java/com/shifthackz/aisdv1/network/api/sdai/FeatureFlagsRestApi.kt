package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.response.FeatureFlagsResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface FeatureFlagsRestApi {

    @GET("/feature-flags.json")
    fun fetchConfig(): Single<FeatureFlagsResponse>
}
