package com.shifthackz.aisdv1.domain.usecase.downloadable

import io.reactivex.rxjava3.core.Single

interface CheckDownloadedModelUseCase {
    operator fun invoke(): Single<Boolean>
}
