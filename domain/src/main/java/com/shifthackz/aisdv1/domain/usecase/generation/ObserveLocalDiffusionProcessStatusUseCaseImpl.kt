package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Observable

internal class ObserveLocalDiffusionProcessStatusUseCaseImpl(
    private val localDiffusionGenerationRepository: LocalDiffusionGenerationRepository,
) : ObserveLocalDiffusionProcessStatusUseCase {

    override fun invoke(): Observable<LocalDiffusion.Status> = localDiffusionGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
