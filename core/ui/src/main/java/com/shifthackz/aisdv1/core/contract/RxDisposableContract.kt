package com.shifthackz.aisdv1.core.contract

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

interface RxDisposableContract {

    val compositeDisposable: CompositeDisposable

    infix operator fun CompositeDisposable.plus(d: Disposable) = this.add(compositeDisposable)

    operator fun Disposable.not() {
        compositeDisposable.add(this)
    }

    fun Disposable.addToDisposable() {
        compositeDisposable.add(this)
    }
}
