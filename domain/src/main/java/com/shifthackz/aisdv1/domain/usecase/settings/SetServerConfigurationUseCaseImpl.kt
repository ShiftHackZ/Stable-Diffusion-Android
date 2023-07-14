package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Completable

internal class SetServerConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : SetServerConfigurationUseCase {

    override fun invoke(url: String, demoMode: Boolean, source: ServerSource): Completable =
        Completable.fromAction {
//            preferenceManager.useSdAiCloud = useSdAiCloud
            preferenceManager.source = source
            preferenceManager.serverUrl = url
            preferenceManager.demoMode = demoMode
        }
}
