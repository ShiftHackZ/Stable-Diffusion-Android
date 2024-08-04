package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Completable

internal class TestConnectivityUseCaseImpl(
    private val repository: StableDiffusionGenerationRepository,
) : TestConnectivityUseCase {

    override fun invoke(url: String): Completable = repository.checkApiAvailability(url)
}
