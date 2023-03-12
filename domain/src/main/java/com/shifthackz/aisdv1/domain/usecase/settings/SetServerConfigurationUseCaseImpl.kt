package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Completable

internal class SetServerConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : SetServerConfigurationUseCase {

    override fun invoke(url: String, demoMode: Boolean): Completable = Completable.fromAction {
        preferenceManager.serverUrl = url
        preferenceManager.demoMode = demoMode
    }
}
