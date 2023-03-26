package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class ImageToImageUseCaseImpl(
    private val generationRepository: StableDiffusionGenerationRepository,
    private val coinRepository: CoinRepository,
) : ImageToImageUseCase {

    override fun invoke(payload: ImageToImagePayload) = generationRepository
        .generateFromImage(payload)
        .flatMap { result ->
            coinRepository
                .spendCoin()
                .andThen(Single.just(result))
        }
}
