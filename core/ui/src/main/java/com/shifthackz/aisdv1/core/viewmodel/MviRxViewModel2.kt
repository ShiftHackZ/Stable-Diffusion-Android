@file:Suppress("unused")

package com.shifthackz.aisdv1.core.viewmodel

import com.shifthackz.aisdv1.core.contract.RxDisposableContract
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviIntent
import com.shifthackz.aisdv1.core.ui.MviState
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class MviRxViewModel2<S : MviState, I : MviIntent, E : MviEffect> : MviViewModel2<S, I, E>(),
    RxDisposableContract {

    override val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
