package com.shifthackz.aisdv1.domain.usecase.version

import com.shifthackz.aisdv1.domain.entity.AppVersion
import io.reactivex.rxjava3.core.Single

interface CheckAppVersionUpdateUseCase {

    operator fun invoke(): Single<Result>

    sealed interface Result {
        object NoUpdateNeeded : Result
        data class NewVersionAvailable(val availableVersion: AppVersion) : Result
    }
}
