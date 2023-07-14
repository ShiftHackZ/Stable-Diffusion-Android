package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

internal class GetConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : GetConfigurationUseCase {

    override fun invoke(): Single<Configuration> = Single.just(
        Configuration(
            preferenceManager.serverUrl,
            preferenceManager.demoMode,
            preferenceManager.source,
            preferenceManager.hordeApiKey,
        )
    )
}
