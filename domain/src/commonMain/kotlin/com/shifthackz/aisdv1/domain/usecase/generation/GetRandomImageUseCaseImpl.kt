package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.RandomImageRepository

class GetRandomImageUseCaseImpl(
    private val randomImageRepository: RandomImageRepository,
) : GetRandomImageUseCase {

    override suspend fun invoke() = randomImageRepository.fetchAndGet()
}
