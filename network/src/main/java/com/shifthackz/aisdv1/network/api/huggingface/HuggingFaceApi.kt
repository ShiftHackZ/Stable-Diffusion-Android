package com.shifthackz.aisdv1.network.api.huggingface

import io.reactivex.rxjava3.core.Completable
import retrofit2.http.GET

interface HuggingFaceApi {

    @GET("/api/whoami-v2")
    fun validateBearerToken(): Completable
}
