package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class GetMonitorConnectivityUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : GetMonitorConnectivityUseCase {

    override fun invoke() = preferenceManager.monitorConnectivity
}

internal class ObserveMonitorConnectivityUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : ObserveMonitorConnectivityUseCase {

    override fun invoke() = preferenceManager
        .observe()
        .map { settings -> settings.monitorConnectivity }
        .distinctUntilChanged()
}
