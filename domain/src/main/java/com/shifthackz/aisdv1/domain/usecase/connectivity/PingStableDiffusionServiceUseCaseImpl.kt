package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.StableDiffusionTextToImageRepository
import io.reactivex.rxjava3.core.Completable

class PingStableDiffusionServiceUseCaseImpl(
    private val repository: StableDiffusionTextToImageRepository,
): PingStableDiffusionServiceUseCase {

    override operator fun invoke(): Completable = repository.checkApiAvailability()
}
