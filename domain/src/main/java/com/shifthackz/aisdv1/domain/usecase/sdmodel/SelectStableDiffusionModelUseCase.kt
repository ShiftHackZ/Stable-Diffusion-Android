package com.shifthackz.aisdv1.domain.usecase.sdmodel

import io.reactivex.rxjava3.core.Completable

interface SelectStableDiffusionModelUseCase {
    operator fun invoke(modelName: String): Completable
}
