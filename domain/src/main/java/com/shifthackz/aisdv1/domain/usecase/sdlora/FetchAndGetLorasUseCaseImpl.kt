package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository

internal class FetchAndGetLorasUseCaseImpl(
    private val stableDiffusionLorasRepository: StableDiffusionLorasRepository,
) : FetchAndGetLorasUseCase {

    override fun invoke() = stableDiffusionLorasRepository.getLoras()
}
