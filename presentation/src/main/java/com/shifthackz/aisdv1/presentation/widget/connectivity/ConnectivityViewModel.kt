package com.shifthackz.aisdv1.presentation.widget.connectivity

import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.connectivity.ObserveSeverConnectivityUseCase
import com.shifthackz.android.core.mvi.EmptyEffect
import com.shifthackz.android.core.mvi.EmptyIntent
import io.reactivex.rxjava3.kotlin.subscribeBy

class ConnectivityViewModel(
    preferenceManager: PreferenceManager,
    observeServerConnectivityUseCase: ObserveSeverConnectivityUseCase,
    schedulersProvider: SchedulersProvider,
) : MviRxViewModel<ConnectivityState, EmptyIntent, EmptyEffect>() {

    override val initialState = ConnectivityState.Uninitialized(preferenceManager.monitorConnectivity)

    init {
        !observeServerConnectivityUseCase()
            .map { connection -> connection to preferenceManager.monitorConnectivity }
            .map(ConnectivityState::consume)
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(::errorLog, EmptyLambda) { state ->
                updateState { state }
            }
    }
}
