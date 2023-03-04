package com.shifthackz.aisdv1.core.common.schedulers

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

fun <T : Any> Single<T>.subscribeOnMainThread(provider: SchedulersProvider): Single<T> = this
    .subscribeOn(provider.io)
    .observeOn(provider.ui)

fun Completable.subscribeOnMainThread(provider: SchedulersProvider): Completable = this
    .subscribeOn(provider.io)
    .observeOn(provider.ui)
