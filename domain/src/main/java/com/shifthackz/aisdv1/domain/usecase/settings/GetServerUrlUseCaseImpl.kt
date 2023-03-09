package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

class GetServerUrlUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : GetServerUrlUseCase {

    override fun invoke(): Single<String> = Single.fromCallable(preferenceManager::serverUrl)
}
