package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import io.reactivex.rxjava3.core.Observable

interface ObserveLocalDiffusionProcessStatusUseCase {
    operator fun invoke(): Observable<LocalDiffusionStatus>
}
