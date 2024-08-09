package com.shifthackz.aisdv1.core.viewmodel

import androidx.lifecycle.ViewModel
import com.shifthackz.aisdv1.core.common.contract.RxDisposableContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class RxViewModel : ViewModel(), RxDisposableContract {

    override val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
