package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import io.reactivex.rxjava3.core.Flowable

interface ObserveHordeProcessStatusUseCase {
    operator fun invoke(): Flowable<HordeProcessStatus>
}
