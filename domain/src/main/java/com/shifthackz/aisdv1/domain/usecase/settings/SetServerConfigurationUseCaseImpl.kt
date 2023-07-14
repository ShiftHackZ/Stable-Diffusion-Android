package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Completable

internal class SetServerConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : SetServerConfigurationUseCase {

    override fun invoke(configuration: Configuration): Completable =
        Completable.fromAction {
            preferenceManager.source = configuration.source
            preferenceManager.serverUrl = configuration.serverUrl
            preferenceManager.demoMode = configuration.demoMode
            preferenceManager.hordeApiKey = configuration.hordeApiKey
        }
}
