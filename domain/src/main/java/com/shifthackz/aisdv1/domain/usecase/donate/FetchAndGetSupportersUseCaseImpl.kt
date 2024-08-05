package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.domain.repository.SupportersRepository
import io.reactivex.rxjava3.core.Single

class FetchAndGetSupportersUseCaseImpl(
    private val supportersRepository: SupportersRepository,
) : FetchAndGetSupportersUseCase {

    override fun invoke(): Single<List<Supporter>> = supportersRepository.fetchAndGetSupporters()
}
