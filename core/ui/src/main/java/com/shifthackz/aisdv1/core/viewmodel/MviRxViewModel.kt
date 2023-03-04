@file:Suppress("unused")

package com.shifthackz.aisdv1.core.viewmodel

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class MviRxViewModel<S : MviState, E : MviEffect> : MviViewModel<S, E>() {

    private val compositeDisposable = CompositeDisposable()

    protected infix operator fun CompositeDisposable.plus(d: Disposable) = this.add(d)

    protected operator fun Disposable.not() {
        compositeDisposable.add(this)
    }

    protected fun Disposable.addToDisposable() {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
