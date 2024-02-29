package com.shifthackz.aisdv1.network.api.huggingface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest
import io.reactivex.rxjava3.core.Single

internal class HuggingFaceInferenceApiImpl(
    private val rawApi: HuggingFaceInferenceApi.RawApi,
) : HuggingFaceInferenceApi {

    override fun generate(
        model: String,
        request: HuggingFaceGenerationRequest,
    ): Single<Bitmap> = rawApi
        .generate(model, request)
        .map { body ->
            val bytes = body.bytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
}
