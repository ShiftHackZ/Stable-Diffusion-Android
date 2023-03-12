package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

internal class GetConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : GetConfigurationUseCase {

    override fun invoke(): Single<Pair<String, Boolean>> = Single.just(
        preferenceManager.serverUrl to preferenceManager.demoMode
    )
}
