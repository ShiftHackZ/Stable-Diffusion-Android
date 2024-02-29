package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface HuggingFaceModelsApi {

    @GET("/hf-models.json")
    fun fetchHuggingFaceModels(): Single<List<HuggingFaceModelRaw>>
}
