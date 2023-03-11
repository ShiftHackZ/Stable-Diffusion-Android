package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Completable

class SetServerUrlUseCaseImpl(
    private val preferenceManager: PreferenceManager,
) : SetServerUrlUseCase {

    override fun invoke(url: String) = Completable.fromAction {
        preferenceManager.serverUrl = url
    }
}
