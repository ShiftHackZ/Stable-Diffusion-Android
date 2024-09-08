package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.response.DownloadableModelResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File

interface DownloadableModelsApi {

    fun fetchOnnxModels(): Single<List<DownloadableModelResponse>>

    fun fetchMediaPipeModels(): Single<List<DownloadableModelResponse>>

    fun <T : Any> downloadModel(
        remoteUrl: String,
        localPath: String,
        stateProgress: (Int) -> T,
        stateComplete: (File) -> T,
        stateFailed: (Throwable) -> T,
    ): Observable<T>

    interface RawApi {
        @GET("/models.json")
        fun fetchOnnxModels(): Single<List<DownloadableModelResponse>>

        @GET("/mediapipe.json")
        fun fetchMediaPipeModels(): Single<List<DownloadableModelResponse>>

        @Streaming
        @GET
        fun downloadModel(@Url url: String): Single<ResponseBody>
    }
}
