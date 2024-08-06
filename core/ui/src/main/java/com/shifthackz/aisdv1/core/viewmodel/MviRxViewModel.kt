@file:Suppress("unused")

package com.shifthackz.aisdv1.core.viewmodel

import com.shifthackz.aisdv1.core.common.contract.RxDisposableContract
import com.shifthackz.android.core.mvi.MviEffect
import com.shifthackz.android.core.mvi.MviIntent
import com.shifthackz.android.core.mvi.MviState
import com.shifthackz.android.core.mvi.MviViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class MviRxViewModel<S : MviState, I : MviIntent, E : MviEffect> : MviViewModel<S, I, E>(),
    RxDisposableContract {

    override val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
