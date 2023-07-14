package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import io.reactivex.rxjava3.core.Completable

interface SetServerConfigurationUseCase {
    operator fun invoke(url: String, demoMode: Boolean, source: ServerSource): Completable
}
