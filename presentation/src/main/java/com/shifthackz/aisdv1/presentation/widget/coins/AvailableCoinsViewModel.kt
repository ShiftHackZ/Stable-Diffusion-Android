package com.shifthackz.aisdv1.presentation.widget.coins

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.coin.ObserveCoinsUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class AvailableCoinsViewModel(
    observeAvailableCoinsUseCase: ObserveCoinsUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<AvailableCoinsState, EmptyEffect>() {

    override val emptyState = AvailableCoinsState.Hidden

    init {
        !observeAvailableCoinsUseCase()
            .subscribeOnMainThread(schedulersProvider)
            .map { result ->
                when (result) {
                    is ObserveCoinsUseCase.Result.Coins -> {
                        AvailableCoinsState.Content(result.value)
                    }
                    else -> AvailableCoinsState.Hidden
                }
            }
            .subscribeBy(::errorLog, EmptyLambda, ::setState)
    }
}
