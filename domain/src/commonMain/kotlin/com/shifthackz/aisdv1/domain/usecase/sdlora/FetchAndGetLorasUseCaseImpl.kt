package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.repository.LorasRepository

class FetchAndGetLorasUseCaseImpl(
    private val lorasRepository: LorasRepository,
) : FetchAndGetLorasUseCase {

    override suspend fun invoke() = lorasRepository.fetchAndGetLoras()
}
