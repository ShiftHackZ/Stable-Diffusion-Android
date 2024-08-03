package com.shifthackz.aisdv1.network.api.swarmui

import android.graphics.Bitmap
import com.shifthackz.aisdv1.network.request.SwarmUiGenerationRequest
import com.shifthackz.aisdv1.network.response.SwarmUiGenerationResponse
import com.shifthackz.aisdv1.network.response.SwarmUiSessionResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url

interface SwarmUiApi {

    fun getNewSession(url: String): Single<SwarmUiSessionResponse>

    fun textToImage(
        @Url url: String,
        @Body request: SwarmUiGenerationRequest,
    ): Single<SwarmUiGenerationResponse>

    fun downloadImage(url: String): Single<Bitmap>

    interface RawApi {

        @POST
        fun getNewSession(@Url url: String, @Body map: Map<String, String>): Single<SwarmUiSessionResponse>

        @POST
        fun textToImage(
            @Url url: String,
            @Body request: SwarmUiGenerationRequest,
        ): Single<SwarmUiGenerationResponse>

        @Streaming
        @GET
        fun download(@Url url: String): Single<Response<ResponseBody>>
    }

    companion object {
        const val PATH_SESSION = "API/GetNewSession"
        const val PATH_TXT_TO_IMG = "API/GenerateText2Image"
    }
}
