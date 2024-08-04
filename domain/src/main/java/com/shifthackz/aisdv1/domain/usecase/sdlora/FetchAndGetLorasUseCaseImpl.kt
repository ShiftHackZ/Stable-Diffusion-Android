package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.repository.LorasRepository

internal class FetchAndGetLorasUseCaseImpl(
    private val lorasRepository: LorasRepository,
) : FetchAndGetLorasUseCase {

    override fun invoke() = lorasRepository.fetchAndGetLoras()
}
