package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import io.reactivex.rxjava3.kotlin.subscribeBy

class ConnectivityViewModel(
    observeServerConnectivityUseCase: ObserveSeverConnectivityUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<ConnectivityState, EmptyEffect>() {

    override val emptyState = ConnectivityState.Uninitialized

    init {
        !observeServerConnectivityUseCase()
            .map(ConnectivityState::consume)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(Throwable::printStackTrace, EmptyLambda, ::setState)
    }
}
