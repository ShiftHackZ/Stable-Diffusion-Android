package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import io.reactivex.rxjava3.core.Observable

interface ObserveLocalDiffusionProcessStatusUseCase {
    operator fun invoke(): Observable<LocalDiffusion.Status>
}
