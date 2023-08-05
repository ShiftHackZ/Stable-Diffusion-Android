package com.shifthackz.aisdv1.network.api.imagecdn

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageCdnRestApi {

    fun fetchRandomImage(): Single<Bitmap>

    interface RawApi {

        @GET("/{size}/{size}")
        fun fetchRandomImage(@Path("size") size: String): Single<ResponseBody>
    }
}
