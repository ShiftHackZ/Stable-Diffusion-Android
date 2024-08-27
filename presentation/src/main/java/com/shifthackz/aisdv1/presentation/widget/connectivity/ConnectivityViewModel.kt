package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.subscribeBy

class ConnectivityViewModel(
    preferenceManager: PreferenceManager,
    observeServerConnectivityUseCase: ObserveSeverConnectivityUseCase,
    dispatchersProvider: DispatchersProvider,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<ConnectivityState, EmptyIntent, EmptyEffect>() {

    override val initialState = ConnectivityState.Uninitialized(preferenceManager.monitorConnectivity)

    override val effectDispatcher = dispatchersProvider.immediate

    init {
        !Flowable.combineLatest(
            observeServerConnectivityUseCase(),
            preferenceManager.observe().map(Settings::monitorConnectivity),
            ::Pair,
        )
            .map(ConnectivityState::consume)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda, ::emitState)
    }
}
