package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

internal class GetConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : GetConfigurationUseCase {

    override fun invoke(): Single<GetConfigurationUseCase.Configuration> = Single.just(
        GetConfigurationUseCase.Configuration(
            preferenceManager.serverUrl,
            preferenceManager.demoMode,
            preferenceManager.useSdAiCloud,
        )
    )
}
