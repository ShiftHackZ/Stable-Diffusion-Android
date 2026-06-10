package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.domain.repository.SupportersRepository

class FetchSupportersUseCaseImpl(
    private val repository: SupportersRepository,
) : FetchSupportersUseCase {

    override suspend fun invoke(): List<Supporter> = repository.fetchAndGetSupporters()
}
