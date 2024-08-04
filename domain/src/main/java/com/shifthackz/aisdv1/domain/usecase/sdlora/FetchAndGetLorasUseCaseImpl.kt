package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetLorasUseCaseImpl(
    private val stableDiffusionLorasRepository: StableDiffusionLorasRepository,
) : FetchAndGetLorasUseCase {

    override fun invoke(): Single<List<StableDiffusionLora>> = stableDiffusionLorasRepository.getLoras()
}
