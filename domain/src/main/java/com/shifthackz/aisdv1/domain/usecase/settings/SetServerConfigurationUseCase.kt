package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import io.reactivex.rxjava3.core.Completable

interface SetServerConfigurationUseCase {
    operator fun invoke(configuration: Configuration): Completable
}
