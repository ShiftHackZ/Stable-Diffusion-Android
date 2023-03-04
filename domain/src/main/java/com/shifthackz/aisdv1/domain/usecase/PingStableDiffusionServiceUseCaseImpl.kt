package com.shifthackz.aisdv1.domain.usecase

import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Completable

class PingStableDiffusionServiceUseCaseImpl(
    private val repository: StableDiffusionTextToImageRepository,
): PingStableDiffusionServiceUseCase {

    override fun execute(): Completable = repository.checkApiAvailability()
}
