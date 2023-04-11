package com.shifthackz.aisdv1.network.extensions

import com.shifthackz.aisdv1.network.exception.BadKeywordException
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException

fun <T: Any> Single<T>.withExceptionMapper(): Single<T> =
    onErrorResumeNext { t -> Single.error(t.mapException()) }

private fun Throwable.mapException(): Throwable {
    if (this is HttpException && code() == 400) {
        val keyword = response()?.errorBody()?.string()
        return keyword?.let(::BadKeywordException) ?: this
    }
    return this
}
