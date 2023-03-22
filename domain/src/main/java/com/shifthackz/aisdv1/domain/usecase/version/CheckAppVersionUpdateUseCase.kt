package com.shifthackz.aisdv1.domain.usecase.version

import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import io.reactivex.rxjava3.core.Single

interface CheckAppVersionUpdateUseCase {

    operator fun invoke(): Single<Result>

    sealed interface Result {
        object NoUpdateNeeded : Result
        data class NewVersionAvailable(val availableVersion: BuildVersion) : Result
    }
}
