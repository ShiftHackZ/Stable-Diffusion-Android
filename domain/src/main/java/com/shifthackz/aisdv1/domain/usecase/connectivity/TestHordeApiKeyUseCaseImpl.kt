package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class TestHordeApiKeyUseCaseImpl(
    private val hordeGenerationRepository: HordeGenerationRepository,
) : TestHordeApiKeyUseCase {

    override fun invoke(): Single<Boolean> = hordeGenerationRepository.validateApiKey()
}
