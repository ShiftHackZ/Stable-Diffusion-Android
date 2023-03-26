package com.shifthackz.aisdv1.core.common.reactive

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

fun <T: Any> Single<T>.retryWithDelay(
    maxRetries: Long,
    delay: Long,
    unit: TimeUnit,
    tryCallback: (Long) -> Unit = {}
): Single<T> {
    return this.retryWhen(FlowableRetryWithDelay(maxRetries, unit.toMillis(delay), tryCallback))
}

fun <T : Any> Observable<T>.retryWithDelay(
    maxRetries: Long,
    delay: Long,
    unit: TimeUnit,
    tryCallback: (Long) -> Unit = {}
): Observable<T> {
    return this.retryWhen(ObservableRetryWithDelay(maxRetries, unit.toMillis(delay), tryCallback))
}

fun <T : Any> Flowable<T>.retryWithDelay(
    maxRetries: Long,
    delay: Long,
    unit: TimeUnit,
    tryCallback: (Long) -> Unit = {}
): Flowable<T> {
    return this.retryWhen(FlowableRetryWithDelay(maxRetries, unit.toMillis(delay), tryCallback))
}

fun Completable.retryWithDelay(
    maxRetries: Long,
    delay: Long,
    unit: TimeUnit,
    tryCallback: (Long) -> Unit = {}
): Completable {
    return this.retryWhen(FlowableRetryWithDelay(maxRetries, unit.toMillis(delay), tryCallback))
}

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
            Observable.error(it)
        }
    }

}

class FlowableRetryWithDelay(
    private val maxRetries: Long,
    private val retryDelayMillis: Long,
    private val tryCallback: (Long) -> Unit
) : Function<Flowable<Throwable>, Publisher<Any>> {

    private var retryCount: Long = 0

    override fun apply(attemps: Flowable<Throwable>): Publisher<Any> {
        return attemps.flatMap {
            if (++retryCount < maxRetries) {
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                return@flatMap Flowable
                    .timer(retryDelayMillis, TimeUnit.MILLISECONDS)
                    .doOnNext {
                        tryCallback(retryCount)
                    }
            }
            // Max retries hit. Just pass the error along.
            Flowable.error<Any>(it)
        }
    }
}
