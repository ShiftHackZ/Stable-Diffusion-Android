package com.shifthackz.aisdv1.network.error

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifthackz.aisdv1.network.response.StabilityAiErrorResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException

class StabilityAiErrorMapper(private val gson: Gson) {

    operator fun <T : Any> invoke(t: Throwable): Single<T> = if (t is HttpException) {
        val errorResponse = gson.fromJson<StabilityAiErrorResponse>(
            t.response()?.errorBody()?.string(),
            object : TypeToken<StabilityAiErrorResponse>() {}.type
        )
        Single.error(errorResponse?.message?.let(::Throwable) ?: t)
    } else {
        Single.error(t)
    }
}
