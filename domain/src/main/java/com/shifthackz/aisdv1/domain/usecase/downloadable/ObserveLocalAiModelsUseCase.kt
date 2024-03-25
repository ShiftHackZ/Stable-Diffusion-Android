package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import io.reactivex.rxjava3.core.Flowable

interface ObserveLocalAiModelsUseCase {
    operator fun invoke(): Flowable<List<LocalAiModel>>
}
