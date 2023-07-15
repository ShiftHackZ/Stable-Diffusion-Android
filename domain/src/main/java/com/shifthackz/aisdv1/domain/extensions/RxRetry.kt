package com.shifthackz.aisdv1.domain.extensions

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import java.util.concurrent.TimeUnit

class ObservableRetryWithDelay(
    private val maxRetries: Long,
    private val retryDelayMillis: Long,
    private val tryCallback: (Long) -> Unit
) : Function<Observable<Throwable>, Observable<Any>> {

    private var retryCount: Long = 0

    override fun apply(attemps: Observable<Throwable>): Observable<Any> {
        return attemps.flatMap {
            if (++retryCount < maxRetries) {
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                return@flatMap Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
                    .doOnNext {
                        tryCallback(retryCount)
                    }
            }
            // Max retries hit. Just pass the error along.
            Observable.error<Any>(it)
        }
    }
}

fun <T : Any> Observable<T>.retryWithDelay(
    maxRetries: Long,
    delay: Long,
    unit: TimeUnit,
    tryCallback: (Long) -> Unit = {},
): Observable<T> {
    return this.retryWhen(ObservableRetryWithDelay(maxRetries, unit.toMillis(delay), tryCallback))
}
