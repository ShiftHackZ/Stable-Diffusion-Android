package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import io.reactivex.rxjava3.core.Flowable

internal class ObserveHordeProcessStatusUseCaseImpl(
    private val hordeGenerationRepository: HordeGenerationRepository,
) : ObserveHordeProcessStatusUseCase {

    override fun invoke(): Flowable<HordeProcessStatus> = hordeGenerationRepository
        .observeStatus()
        .distinctUntilChanged()
}
