package com.shifthackz.aisdv1.network.api.sdai

import com.shifthackz.aisdv1.network.request.ReportRequest
import io.reactivex.rxjava3.core.Completable
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApi {

    @POST("/report")
    fun postReport(@Body request: ReportRequest): Completable
}
