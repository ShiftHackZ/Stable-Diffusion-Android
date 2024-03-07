package com.shifthackz.aisdv1.network.api.huggingface

import android.graphics.Bitmap
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface HuggingFaceInferenceApi {

    fun generate(
        model: String,
        request: HuggingFaceGenerationRequest,
    ): Single<Bitmap>

    interface RawApi {
        @Streaming
        @POST("/models/{model}")
        fun generate(
            @Path("model") model: String,
            @Body request: HuggingFaceGenerationRequest,
        ): Single<Response<ResponseBody>>
    }
}
