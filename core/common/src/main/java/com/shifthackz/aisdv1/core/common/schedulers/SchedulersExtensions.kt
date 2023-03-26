package com.shifthackz.aisdv1.core.common.schedulers

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

fun <T : Any> Single<T>.subscribeOnMainThread(provider: SchedulersProvider): Single<T> = this
    .subscribeOn(provider.io)
    .observeOn(provider.ui)

fun <T : Any> Observable<T>.subscribeOnMainThread(provider: SchedulersProvider): Observable<T> =
    this
        .subscribeOn(provider.io)
        .observeOn(provider.ui)

fun <T : Any> Flowable<T>.subscribeOnMainThread(provider: SchedulersProvider): Flowable<T> =
    this
        .subscribeOn(provider.io)
        .observeOn(provider.ui)


fun Completable.subscribeOnMainThread(provider: SchedulersProvider): Completable = this
    .subscribeOn(provider.io)
    .observeOn(provider.ui)
