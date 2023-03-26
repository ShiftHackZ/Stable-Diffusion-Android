package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class TextToImageUseCaseImpl(
    private val generationRepository: StableDiffusionGenerationRepository,
    private val coinRepository: CoinRepository,
) : TextToImageUseCase {

    override operator fun invoke(payload: TextToImagePayload) = generationRepository
        .generateFromText(payload)
        .flatMap { result ->
            coinRepository.spendCoin()
                .andThen(Single.just(result))
        }
}
